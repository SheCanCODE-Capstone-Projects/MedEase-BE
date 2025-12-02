package com.springboot.medease.Services;
import com.springboot.medease.GlobalException.ResourceNotFoundException;
import com.springboot.medease.Models.RegisterResponse;
import com.springboot.medease.Models.User;
import com.springboot.medease.Models.UserType;
import com.springboot.medease.Repository.UserRepository;
import com.springboot.medease.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public RegisterResponse register(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResourceNotFoundException("Email already exists");
        }
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new ResourceNotFoundException("Phone number already exists");
        }
        if (user.getUserType() == null) {
            user.setUserType(UserType.patient);
        }

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser);

        return new RegisterResponse(
                "User registered successfully",
                savedUser.getId(),
                token
        );
    }

}

