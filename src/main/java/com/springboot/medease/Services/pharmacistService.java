package com.springboot.medease.Services;

import com.springboot.medease.DTOs.PharmacistRegisterRequest;
import com.springboot.medease.DTOs.pharmacistRegisterResponse;
import com.springboot.medease.GlobalException.DuplicateResourceException;
import com.springboot.medease.Models.User;
import com.springboot.medease.Models.UserType;
import com.springboot.medease.Repository.UserRepository;
import com.springboot.medease.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class pharmacistService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public pharmacistRegisterResponse registerPharmacist(PharmacistRegisterRequest request) {

        if (userRepository.existsByEmail(request.getPharmacyEmail())) {
            throw new DuplicateResourceException("Pharmacy email already exists!");
        }

        if (userRepository.existsByPharmacistLicenseNumber(request.getPharmacistLicenseNumber())) {
            throw new DuplicateResourceException("License number already exists!");
        }

        User user = new User();
        user.setPharmacistFirstName(request.getPharmacistFirstName());
        user.setPharmacistLastName(request.getPharmacistLastName());
        user.setPharmacistLicenseNumber(request.getPharmacistLicenseNumber());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPharmacyName(request.getPharmacyName());
        user.setPharmacyEmail(request.getPharmacyEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserType(UserType.PHARMACIST);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);

        return new pharmacistRegisterResponse(
                "User registered successfully",
                user.getId(),
                user.getPharmacyEmail(),
                user.getUserType(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                token
        );
    }
}

