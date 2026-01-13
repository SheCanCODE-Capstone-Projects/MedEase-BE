package com.springboot.medease.Services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.springboot.medease.DTOs.*;
import com.springboot.medease.GlobalException.DuplicateResourceException;
import com.springboot.medease.Models.*;
import com.springboot.medease.Repository.PasswordResetTokenRepository;
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
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private static final String CONTAINER_ID = "MAIN_USER_CONTAINER";

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       PatientService patientService,
                       PatientRepository patientRepository,
                       @Value("${google.client.id}") String googleClientId,
                       OTPService otpService,
                       EmailService emailService,
                       PasswordResetTokenRepository passwordResetTokenRepository) {
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
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    private record ProfileAndRole(Profile profile, String role) {}

    private ProfileAndRole findProfile(User container, String identifier) {
        boolean isEmail = identifier.contains("@");

        Map<String, List<? extends Profile>> rolesMap = Map.of(
                "ROLE_PATIENT", Optional.ofNullable(container.getPatients()).orElse(Collections.emptyList()),
                "ROLE_DOCTOR", Optional.ofNullable(container.getDoctors()).orElse(Collections.emptyList()),
                "ROLE_PHARMACIST", Optional.ofNullable(container.getPharmacists()).orElse(Collections.emptyList())
        );

        for (var entry : rolesMap.entrySet()) {
            for (Profile profile : entry.getValue()) {
                if ((isEmail && profile.getEmail().equals(identifier)) ||
                        (!isEmail && profile.getPhoneNumber().equals(identifier))) {
                    return new ProfileAndRole(profile, entry.getKey());
                }
            }
        }
        return null;
    }

    public AuthResponse registerPatient(PatientRegisterRequest req) {
        User container = userRepository.findById(CONTAINER_ID).orElse(new User());
        container.setId(CONTAINER_ID);

        List<PatientProfile> patients = container.getPatients() != null ? container.getPatients() : new ArrayList<>();
        container.setPatients(patients);

        if (patients.stream().anyMatch(p -> p.getEmail().equals(req.getEmail().trim())))
            throw new DuplicateResourceException("Email already exists");
        if (patients.stream().anyMatch(p -> p.getPhoneNumber().equals(req.getPhoneNumber().trim())))
            throw new DuplicateResourceException("Phone number already exists");

        PatientProfile profile = new PatientProfile();
        profile.setFirstName(req.getFirstName());
        profile.setLastName(req.getLastName());
        profile.setEmail(req.getEmail());
        profile.setPhoneNumber(req.getPhoneNumber());
        profile.setPassword(passwordEncoder.encode(req.getPassword()));
        profile.setInsuranceProvider(req.getInsuranceProvider());
        profile.setInsuranceNumber(req.getInsuranceNumber());

        patients.add(profile);
        userRepository.save(container);

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
                    User u = new User();
                    u.setId(CONTAINER_ID);
                    return u;
                });

        List<PharmacistProfile> pharmacists = container.getPharmacists() != null ? container.getPharmacists() : new ArrayList<>();
        container.setPharmacists(pharmacists);

        if (pharmacists.stream().anyMatch(p -> p.getEmail().equals(req.getEmail().trim())))
            throw new DuplicateResourceException("Email already exists");
        if (pharmacists.stream().anyMatch(p -> p.getPhoneNumber().equals(req.getPhoneNumber().trim())))
            throw new DuplicateResourceException("Phone number already exists");

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

        String token = jwtUtil.generateToken(profile, UserType.ROLE_PHARMACIST);

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

        ProfileAndRole profileAndRole = findProfile(container, req.getIdentifier().trim());
        if (profileAndRole == null || !passwordEncoder.matches(req.getPassword(), profileAndRole.profile().getPassword()))
            throw new BadCredentialsException("Invalid credentials");

        String token = jwtUtil.generateToken(profileAndRole.profile(), UserType.valueOf(profileAndRole.role()));

        return new AuthResponse(
                "Login successful",
                container.getId(),
                req.getIdentifier(),
                UserType.valueOf(profileAndRole.role()),
                container.getCreatedAt(),
                container.getUpdatedAt(),
                token
        );
    }

    public AuthResponse googleLogin(String idTokenString) {
        GoogleIdToken idToken;
        try { 
            idToken = googleIdTokenVerifier.verify(idTokenString); 
        } catch (GeneralSecurityException | IOException e) {
            throw new BadCredentialsException("Invalid Google token: " + e.getMessage(), e);
        }
        if (idToken == null) throw new BadCredentialsException("Invalid Google token");

        String email = idToken.getPayload().getEmail();
        if (!Boolean.TRUE.equals(idToken.getPayload().getEmailVerified()))
            throw new BadCredentialsException("Email not verified by Google");

        User container = userRepository.findById(CONTAINER_ID)
                .orElseThrow(() -> new BadCredentialsException("No users found"));

        ProfileAndRole profileAndRole = findProfile(container, email);
        if (profileAndRole == null) throw new BadCredentialsException("User not registered with this email");
        if ("ROLE_DOCTOR".equals(profileAndRole.role()))
            throw new BadCredentialsException("Doctors are not allowed to login using Google");

        String token = jwtUtil.generateToken(profileAndRole.profile().getEmail(), profileAndRole.role());

        return new AuthResponse(
                "Google login successful",
                container.getId(),
                profileAndRole.profile().getEmail(),
                UserType.valueOf(profileAndRole.role()),
                container.getCreatedAt(),
                container.getUpdatedAt(),
                token
        );
    }

    public void sendLoginOtp(String identifier, String password) {
        User container = userRepository.findById(CONTAINER_ID)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        ProfileAndRole profileAndRole = findProfile(container, identifier);
        if (profileAndRole == null || !passwordEncoder.matches(password, profileAndRole.profile().getPassword()))
            throw new BadCredentialsException("Invalid credentials");

        String otp = otpService.generateOtp(profileAndRole.profile().getEmail());
        emailService.sendOtpEmail(profileAndRole.profile().getEmail(), otp);
    }

    public AuthResponse verifyOtpAndLogin(String otp) {
        String identifier = otpService.validateOtp(otp);
        if (identifier == null) throw new BadCredentialsException("Invalid or expired OTP");

        User container = userRepository.findById(CONTAINER_ID)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        ProfileAndRole profileAndRole = findProfile(container, identifier);
        if (profileAndRole == null) throw new BadCredentialsException("User not found");

        String token = jwtUtil.generateToken(profileAndRole.profile().getEmail(), profileAndRole.role());

        return new AuthResponse(
                "Login successful",
                container.getId(),
                identifier,
                UserType.valueOf(profileAndRole.role()),
                container.getCreatedAt(),
                container.getUpdatedAt(),
                token
        );
    }

    public void sendResetPasswordLink(String identifier) {
        User container = userRepository.findById(CONTAINER_ID)
                .orElseThrow(() -> new BadCredentialsException("No users found"));

        ProfileAndRole profileAndRole = findProfile(container, identifier);
        if (profileAndRole == null) throw new BadCredentialsException("User not found");

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

        PasswordResetToken resetToken = new PasswordResetToken(null, profileAndRole.profile().getEmail(), token, expiresAt);
        passwordResetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        emailService.sendResetPasswordEmail(profileAndRole.profile().getEmail(), resetLink);
    }

    public AuthResponse resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new BadCredentialsException("Passwords do not match");

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new BadCredentialsException("Token expired");

        User container = userRepository.findById(CONTAINER_ID)
                .orElseThrow(() -> new BadCredentialsException("User container not found"));

        ProfileAndRole profileAndRole = findProfile(container, resetToken.getEmail());
        if (profileAndRole == null) throw new BadCredentialsException("User not found");

        Profile profile = profileAndRole.profile();
        profile.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(container);

        passwordResetTokenRepository.delete(resetToken);

        String token = jwtUtil.generateToken(profile.getEmail(), profileAndRole.role());

        return new AuthResponse(
                "Password reset successful, now you can log in again in your account",
                container.getId(),
                profile.getEmail(),
                UserType.valueOf(profileAndRole.role()),
                container.getCreatedAt(),
                container.getUpdatedAt(),
                token
        );
    }
}