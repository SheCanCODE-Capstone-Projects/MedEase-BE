package com.springboot.medease.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String message;
    private String userId;
    private String identifier;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String token;
}
