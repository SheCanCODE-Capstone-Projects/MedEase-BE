package com.springboot.medease.Services;

import com.springboot.medease.DTOs.*;
import com.springboot.medease.GlobalException.DuplicateResourceException;
import com.springboot.medease.Models.*;
import com.springboot.medease.Repository.UserRepository;
import com.springboot.medease.Security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PatientService patientService;

    // ID of the container document (singleton)
    private static final String CONTAINER_ID = "MAIN_USER_CONTAINER";

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       PatientService patientService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.patientService = patientService;
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

        // Create Patient document in patients collection
        Patient patient = new Patient();
        patient.setFirstName(profile.getFirstName());
        patient.setLastName(profile.getLastName());
        patient.setEmail(profile.getEmail());
        patient.setPhoneNumber(profile.getPhoneNumber());
        patient.setDateOfBirth(profile.getDateOfBirth());
        patient.setGender(profile.getGender());
        patient.setInsuranceProvider(profile.getInsuranceProvider());
        patient.setInsuranceNumber(profile.getInsuranceNumber());
        patient.setUserType(UserType.ROLE_PATIENT);
        patientService.addPatient(patient);

        String token = jwtUtil.generateToken(profile, UserType.ROLE_PATIENT);

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

        String token = jwtUtil.generateToken(savedProfile, UserType.ROLE_PHARMACIST);


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
        List<PatientProfile> patients = container.getPatients() != null ? container.getPatients() : List.of();
        profile = patients.stream()
                .filter(p -> isEmail ? p.getEmail().equals(identifier) : p.getPhoneNumber().equals(identifier))
                .findFirst()
                .orElse(null);
        if (profile != null) userType = UserType.ROLE_PATIENT;
        if (profile == null) {
            List<DoctorProfile> doctors = container.getDoctors() != null ? container.getDoctors() : List.of();
            profile = doctors.stream()
                    .filter(d -> isEmail ? d.getEmail().equals(identifier) : d.getPhoneNumber().equals(identifier))
                    .findFirst()
                    .orElse(null);
            if (profile != null) userType = UserType.ROLE_DOCTOR;
        }

        if (profile == null) {
            List<PharmacistProfile> pharmacists = container.getPharmacists() != null ? container.getPharmacists() : List.of();
            profile = pharmacists.stream()
                    .filter(ph -> isEmail ? ph.getEmail().equals(identifier) : ph.getPhoneNumber().equals(identifier))
                    .findFirst()
                    .orElse(null);
            if (profile != null) userType = UserType.ROLE_PHARMACIST;
        }

        if (profile == null) throw new BadCredentialsException("Invalid credentials");
        String encodedPassword = null;
        if (profile instanceof PatientProfile p) encodedPassword = p.getPassword();
        if (profile instanceof DoctorProfile d) encodedPassword = d.getPassword();
        if (profile instanceof PharmacistProfile ph) encodedPassword = ph.getPassword();

        if (!passwordEncoder.matches(req.getPassword(), encodedPassword))
            throw new BadCredentialsException("Invalid credentials");


        String token = jwtUtil.generateToken(profile, userType);

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
}
