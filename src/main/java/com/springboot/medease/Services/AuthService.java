package com.springboot.medease.Services;

import com.springboot.medease.DTOs.*;
import com.springboot.medease.GlobalException.DuplicateResourceException;
import com.springboot.medease.Models.PatientProfile;
import com.springboot.medease.Models.PharmacistProfile;
import com.springboot.medease.Models.User;
import com.springboot.medease.Models.UserType;
import com.springboot.medease.Repository.UserRepository;
import com.springboot.medease.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse registerPatient(RegisterRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (userRepository.existsByPhoneNumber(req.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already exists");
        }

        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setFirstName(req.getFirstName());
        patientProfile.setLastName(req.getLastName());
        patientProfile.setInsuranceProvider(req.getInsuranceProvider());
        patientProfile.setInsuranceNumber(req.getInsuranceNumber());


        User user = new User();
        user.setEmail(req.getEmail());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setUserType(UserType.ROLE_PATIENT);
        user.setPatientProfile(patientProfile);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());


        userRepository.save(user);

        String token = jwtUtil.generateToken(user);

        return new AuthResponse("User registered successfully",
                user.getId(),
                user.getEmail(),
                user.getUserType().name(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                token);
    }

    public AuthResponse registerPharmacist(PharmacistRegisterRequest req) {

        if (userRepository.existsByEmail(req.getPharmacyEmail())) {
            throw new DuplicateResourceException("Pharmacy email already exists");
        }
        Boolean exists = userRepository.existsByPharmacistProfilePharmacistLicenseNumber(req.getPharmacistLicenseNumber());
        if (Boolean.TRUE.equals(exists)) {
            throw new DuplicateResourceException("License number already exists");
        }

        if (userRepository.existsByPhoneNumber(req.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already exists");
        }

        PharmacistProfile profile = new PharmacistProfile();
        profile.setPharmacistFirstName(req.getPharmacistFirstName());
        profile.setPharmacistLastName(req.getPharmacistLastName());
        profile.setPharmacistLicenseNumber(req.getPharmacistLicenseNumber());
        profile.setPharmacyName(req.getPharmacyName());
        profile.setPharmacyEmail(req.getPharmacyEmail());

        User user = new User();
        user.setEmail(req.getPharmacyEmail());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setUserType(UserType.ROLE_PHARMACIST);
        user.setPharmacistProfile(profile);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());


        userRepository.save(user);

        String token = jwtUtil.generateToken(user);

        return new AuthResponse("Pharmacist registered successfully",
                user.getId(),
                user.getEmail(),
                user.getUserType().name(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                token);
    }

//    public AuthResponse login(LoginRequest req) {
//
//        Optional<User> opt;
//        if (req.getIdentifier().contains("@")) {
//            opt = userRepository.findByEmail(req.getIdentifier());
//        } else {
//            opt = userRepository.findByPhoneNumber(req.getIdentifier());
//        }
//
//        User user = opt.orElseThrow(() -> new RuntimeException("Invalid credentials"));
//
//        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
//            throw new RuntimeException("Invalid credentials");
//        }
//
//        String role = null;
//        if (user.getUserType() != null) {
//            role = user.getUserType().name();
//        } else if (user.getPatientProfile() != null) {
//            role = "ROLE_PATIENT";
//        } else if (user.getDoctorProfile() != null) {
//            role = "ROLE_DOCTOR";
//        } else if (user.getPharmacistProfile() != null) {
//            role = "ROLE_PHARMACIST";
//        }
//
//        String identifier = user.getEmail() != null ? user.getEmail() : user.getPhoneNumber();
//        String token = jwtUtil.generateToken(user);
//
//        return new AuthResponse(
//                "Login successful",
//                user.getId(),
//                identifier,
//                role,
//                user.getCreatedAt(),
//                user.getUpdatedAt(),
//                token
//        );
//    }

}
