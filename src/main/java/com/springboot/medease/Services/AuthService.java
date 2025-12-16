package com.springboot.medease.Services;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.springboot.medease.DTOs.*;
import com.springboot.medease.GlobalException.DuplicateResourceException;
import com.springboot.medease.Models.*;
import com.springboot.medease.Repository.PatientRepository;
import com.springboot.medease.Repository.UserRepository;
import com.springboot.medease.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Collections;

@Service
public class AuthService {

    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PatientService patientService;
    private final PatientRepository patientRepository;
    private final OTPService otpService;
    private final EmailService emailService;

    // ID of the container document (singleton)
    private static final String CONTAINER_ID = "MAIN_USER_CONTAINER";

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       PatientService patientService,
                       PatientRepository patientRepository,
                       @Value("${google.client.id}") String googleClientId,
                       OTPService otpService,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.patientService = patientService;
        this.patientRepository = patientRepository;
        this.googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new GsonFactory()
        ).setAudience(List.of(googleClientId)).build();
        this.otpService = otpService;
        this.emailService = emailService;

    }

    public AuthResponse registerPatient(PatientRegisterRequest req) {

        User container = userRepository.findById(CONTAINER_ID).orElse(new User());
        container.setId(CONTAINER_ID);

        List<PatientProfile> patients = container.getPatients() != null ? container.getPatients() : new ArrayList<>();
        container.setPatients(patients);

        boolean emailExists = patients.stream()
                .anyMatch(p -> p.getEmail().equals(req.getEmail().trim()));
        boolean phoneExists = patients.stream()
                .anyMatch(p -> p.getPhoneNumber().equals(req.getPhoneNumber().trim()));

        if (emailExists) throw new DuplicateResourceException("Email already exists");
        if (phoneExists) throw new DuplicateResourceException("Phone number already exists");

        PatientProfile profile = new PatientProfile();
        profile.setFirstName(req.getFirstName());
        profile.setLastName(req.getLastName());
        profile.setEmail(req.getEmail());
        profile.setPhoneNumber(req.getPhoneNumber());
        profile.setDateOfBirth(req.getDateOfBirth());
        profile.setGender(req.getGender());
        profile.setPassword(passwordEncoder.encode(req.getPassword()));
        profile.setInsuranceProvider(req.getInsuranceProvider());
        profile.setInsuranceNumber(req.getInsuranceNumber());

        if (container.getPatients() == null) {
                        container.setPatients(new ArrayList<>());
        }

        patients.add(profile);


        userRepository.save(container);

      Patient patient = new Patient();
      patient.setFirstName(req.getFirstName());
      patient.setLastName(req.getLastName());
      patient.setEmail(req.getEmail());
      patient.setGender(req.getGender());
      patient.setPassword(passwordEncoder.encode(req.getPassword()));
      patient.setPhoneNumber(profile.getPhoneNumber());
      patient.setDateOfBirth(profile.getDateOfBirth());
      patient.setGender(profile.getGender());
      patient.setInsuranceProvider(profile.getInsuranceProvider());
      patient.setInsuranceNumber(profile.getInsuranceNumber());
      patient.setUserType(UserType.ROLE_PATIENT);
      patient.setPatientReference(generateUniqueReference());

      patientRepository.save(patient);

      String token = jwtUtil.generateToken(profile.getEmail(), "ROLE_PATIENT");

      return new AuthResponse(
                "User registered successfully",
                container.getId(),
                profile.getEmail(),
                UserType.ROLE_PATIENT,
                LocalDateTime.now(),
                LocalDateTime.now(),
                token
        );
    }

    public AuthResponse registerPharmacist(PharmacistRegisterRequest req) {

        User container = userRepository.findById(CONTAINER_ID)
                .orElseGet(() -> {
                    User newContainer = new User();
                    newContainer.setId(CONTAINER_ID);
                    return newContainer;
                });
        List<PharmacistProfile> pharmacists = container.getPharmacists() != null ? container.getPharmacists() : new ArrayList<>();
        container.setPharmacists(pharmacists);

        boolean emailExists = pharmacists.stream()
                              .anyMatch(p -> p.getEmail().equals(req.getEmail().trim()));

        boolean phoneExists = pharmacists.stream()
                       .anyMatch(p -> p.getPhoneNumber().equals(req.getPhoneNumber().trim()));

        if (emailExists) throw new DuplicateResourceException("Email already exists");
        if (phoneExists) throw new DuplicateResourceException("Phone number already exists");

        PharmacistProfile profile = new PharmacistProfile();
        profile.setFirstName(req.getFirstName());
        profile.setLastName(req.getLastName());
        profile.setEmail(req.getEmail().trim());
        profile.setPhoneNumber(req.getPhoneNumber().trim());
        profile.setPassword(passwordEncoder.encode(req.getPassword()));
        profile.setPharmacyName(req.getPharmacyName());
        profile.setPharmacistLicenseNumber(req.getPharmacistLicenseNumber());


        pharmacists.add(profile);


        userRepository.save(container);


        PharmacistProfile savedProfile = container.getPharmacists().stream()
                .filter(ph -> ph.getEmail().equals(profile.getEmail()))
                .findFirst()
                .orElse(profile);

        String token = jwtUtil.generateToken(savedProfile.getEmail(), "ROLE_PHARMACIST");


        return new AuthResponse(
                "User registered successfully",
                container.getId(),
                profile.getEmail(),
                UserType.ROLE_PHARMACIST,
                LocalDateTime.now(),
                LocalDateTime.now(),
                token
        );
    }

    public AuthResponse login(LoginRequest req) {

        User container = userRepository.findById(CONTAINER_ID)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        String identifier = req.getIdentifier().trim();
        boolean isEmail = identifier.contains("@");

        Object profile ;
        UserType userType = null;


        List<PatientProfile> patients = container.getPatients() != null ? container.getPatients() : Collections.emptyList();
        profile = patients.stream()
                .filter(p -> isEmail ? p.getEmail().equals(identifier) : p.getPhoneNumber().equals(identifier))
                .findFirst()
                .orElse(null);
        if (profile != null) userType = UserType.ROLE_PATIENT;


        if (profile == null) {
            List<DoctorProfile> doctors = container.getDoctors() != null ? container.getDoctors() : Collections.emptyList();
            profile = doctors.stream()
                    .filter(d -> isEmail ? d.getEmail().equals(identifier) : d.getPhoneNumber().equals(identifier))
                    .findFirst()
                    .orElse(null);
            if (profile != null) userType = UserType.ROLE_DOCTOR;
        }

        if (profile == null) {
            List<PharmacistProfile> pharmacists = container.getPharmacists() != null ? container.getPharmacists() : Collections.emptyList();
            profile = pharmacists.stream()
                    .filter(ph -> isEmail ? ph.getEmail().equals(identifier) : ph.getPhoneNumber().equals(identifier))
                    .findFirst()
                    .orElse(null);
            if (profile != null) userType = UserType.ROLE_PHARMACIST;
        }

        if (profile == null) throw new BadCredentialsException("Invalid credentials");
        String encodedPassword = null;
        if (profile instanceof PatientProfile) {
            PatientProfile p = (PatientProfile) profile;
            encodedPassword = p.getPassword();
        }
        if (profile instanceof DoctorProfile) {
            DoctorProfile d = (DoctorProfile) profile;
            encodedPassword = d.getPassword();
        }
        if (profile instanceof PharmacistProfile) {
            PharmacistProfile ph = (PharmacistProfile) profile;
            encodedPassword = ph.getPassword();
        }

        if (!passwordEncoder.matches(req.getPassword(), encodedPassword))
            throw new BadCredentialsException("Invalid credentials");


        String token = jwtUtil.generateToken(((Profile) profile).getEmail(), userType.toString());

        return new AuthResponse(
                "Login successful",
                container.getId(),
                isEmail ? ((Profile) profile).getEmail() : ((Profile) profile).getPhoneNumber(),
                userType,
                container.getCreatedAt(),
                container.getUpdatedAt(),
                token
        );
    }

    private String generateUniqueReference() {
        String reference;
        do {
            reference = "PAT-" + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();
        }
        while (patientRepository.existsByPatientReference(reference));
        return reference;
    }

    // Record to hold profile and role
    private record ProfileAndRole(Profile profile, String role) {}

    // Helper method to find profile and role by email
    private ProfileAndRole findProfileByEmail(User container, String email) {

        // Check patients
        List<PatientProfile> patients = container.getPatients() != null ? container.getPatients() : new ArrayList<>();
        Profile profile = patients.stream()
                .filter(p -> p.getEmail().equals(email))
                .findFirst()
                .orElse(null);
        if (profile != null) return new ProfileAndRole(profile, "ROLE_PATIENT");

        // Check doctors
        List<DoctorProfile> doctors = container.getDoctors() != null ? container.getDoctors() : new ArrayList<>();
        profile = doctors.stream()
                .filter(d -> d.getEmail().equals(email))
                .findFirst()
                .orElse(null);
        if (profile != null) return new ProfileAndRole(profile, "ROLE_DOCTOR");

        // Check pharmacists
        List<PharmacistProfile> pharmacists = container.getPharmacists() != null ? container.getPharmacists() : new ArrayList<>();
        profile = pharmacists.stream()
                .filter(ph -> ph.getEmail().equals(email))
                .findFirst()
                .orElse(null);
        if (profile != null) return new ProfileAndRole(profile, "ROLE_PHARMACIST");

        return null; // Not found
    }


    public AuthResponse googleLogin(String idTokenString) {

        GoogleIdToken idToken;
        try {
            idToken = googleIdTokenVerifier.verify(idTokenString);
        } catch (GeneralSecurityException | IOException e) {
            throw new BadCredentialsException("Invalid Google token" + e.getMessage(), e);
        }

        if (idToken == null) {
            throw new BadCredentialsException("Invalid Google token");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();

        Boolean emailVerified = payload.getEmailVerified();
        if (!Boolean.TRUE.equals(emailVerified)) {
            throw new BadCredentialsException("Email not verified by Google");
        }

        // Load container
        User container = userRepository.findById(CONTAINER_ID)
                .orElseThrow(() -> new BadCredentialsException("No users found"));


        var profileAndRole = findProfileByEmail(container, email);

        if (profileAndRole == null) {
            throw new BadCredentialsException("User not registered with this email");
        }

        // BLOCK DOCTORS FROM GOOGLE LOGIN
        if ("ROLE_DOCTOR".equals(profileAndRole.role())) {
            throw new BadCredentialsException("Doctors are not allowed to login using Google");
        }

        // Generate JWT
        String token = jwtUtil.generateToken(profileAndRole.profile.getEmail(), profileAndRole.role());

        return new AuthResponse(
                "Google login successful",
                container.getId(),
                profileAndRole.profile.getEmail(),
                UserType.valueOf(profileAndRole.role()),
                container.getCreatedAt(),
                container.getUpdatedAt(),
                token
        );
    }


    private ProfileAndRole findProfileByIdentifier(User container, String identifier) {
        boolean isEmail = identifier.contains("@");


        List<PatientProfile> patients = container.getPatients() != null ? container.getPatients() : new ArrayList<>();
        for (PatientProfile p : patients) {
            if ((isEmail && p.getEmail().equals(identifier)) || (!isEmail && p.getPhoneNumber().equals(identifier))) {
                return new ProfileAndRole(p, "ROLE_PATIENT");
            }
        }


        List<DoctorProfile> doctors = container.getDoctors() != null ? container.getDoctors() : new ArrayList<>();
        for (DoctorProfile d : doctors) {
            if ((isEmail && d.getEmail().equals(identifier)) || (!isEmail && d.getPhoneNumber().equals(identifier))) {
                return new ProfileAndRole(d, "ROLE_DOCTOR");
            }
        }


        List<PharmacistProfile> pharmacists = container.getPharmacists() != null ? container.getPharmacists() : new ArrayList<>();
        for (PharmacistProfile ph : pharmacists) {
            if ((isEmail && ph.getEmail().equals(identifier)) || (!isEmail && ph.getPhoneNumber().equals(identifier))) {
                return new ProfileAndRole(ph, "ROLE_PHARMACIST");
            }
        }

        return null;
    }


    public void sendLoginOtp(String identifier, String password) {

        User container = userRepository.findById(CONTAINER_ID)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        // 1. Verify identifier and password first
        ProfileAndRole profileAndRole = findProfileByIdentifier(container, identifier);
        if (profileAndRole == null || !passwordEncoder.matches(password, profileAndRole.profile().getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // 2. Generate OTP
        String otp = otpService.generateOtp(profileAndRole.profile().getEmail());

        // 3. Send OTP via email
        emailService.sendOtpEmail(profileAndRole.profile().getEmail(), otp);
    }


    public String verifyOtpAndLogin(String otp) {
        String email = otpService.validateOtp(otp);
        if (email == null) {
            throw new BadCredentialsException("Invalid or expired OTP");
        }

        // 1. Find the user again
        User container = userRepository.findById(CONTAINER_ID)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        ProfileAndRole profileAndRole = findProfileByEmail(container, email);
        if (profileAndRole == null) {
            throw new BadCredentialsException("User not found");
        }

        // 2. Generate JWT
        return jwtUtil.generateToken(profileAndRole.profile().getEmail(), profileAndRole.role());
    }


}
