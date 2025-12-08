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

        if (userRepository.existsByPatientsEmail(req.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (userRepository.existsByPatientsPhoneNumber(req.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already exists");
        }

        PatientProfile profile = new PatientProfile();
        profile.setFirstName(req.getFirstName());
        profile.setLastName(req.getLastName());
        profile.setEmail(req.getEmail());
        profile.setPhoneNumber(req.getPhoneNumber());
        profile.setPassword(passwordEncoder.encode(req.getPassword()));
        profile.setInsuranceProvider(req.getInsuranceProvider());
        profile.setInsuranceNumber(req.getInsuranceNumber());

        User user = new User();
        user.setPatients(profile);

        userRepository.save(user);

        String token = jwtUtil.generateToken(profile.getEmail());

        return new AuthResponse(
                "User registered successfully",
                user.getId(),
                profile.getEmail(),
                "PATIENT",
                LocalDateTime.now(),
                LocalDateTime.now(),
                token
        );
    }


    public AuthResponse registerPharmacist(PharmacistRegisterRequest req) {

        if (userRepository.existsByPharmacistsPharmacyEmail(req.getPharmacyEmail())) {
            throw new DuplicateResourceException("Pharmacy email already exists");
        }

        if (userRepository.existsByPharmacistsPharmacistLicenseNumber(req.getPharmacistLicenseNumber())) {
            throw new DuplicateResourceException("License number already exists");
        }

        if (userRepository.existsByPharmacistsPhoneNumber(req.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already exists");
        }

        PharmacistProfile profile = new PharmacistProfile();
        profile.setFirstName(req.getPharmacistFirstName());
        profile.setLastName(req.getPharmacistLastName());
        profile.setEmail(req.getPharmacyEmail());
        profile.setPhoneNumber(req.getPhoneNumber());
        profile.setPassword(passwordEncoder.encode(req.getPassword()));
        profile.setPharmacistLicenseNumber(req.getPharmacistLicenseNumber());
        profile.setPharmacyName(req.getPharmacyName());
        profile.setPharmacyEmail(req.getPharmacyEmail());

        User user = new User();
        user.setPharmacists(profile);

        userRepository.save(user);

        String token = jwtUtil.generateToken(profile.getEmail());

        return new AuthResponse(
                "User registered successfully",
                user.getId(),
                profile.getEmail(),
                "PHARMACIST",
                LocalDateTime.now(),
                LocalDateTime.now(),
                token
        );
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
