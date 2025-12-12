package com.springboot.medease.DTOs;

import com.springboot.medease.Models.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private String userId;
    private String email;
    private UserType userType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String token;
}
