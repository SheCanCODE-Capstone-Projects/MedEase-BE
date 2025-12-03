package com.springboot.medease.DTOs;

import com.springboot.medease.Models.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class pharmacistRegisterResponse {

    private String message;
    private String id;
    private String pharmacyEmail;
    private UserType userType;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private String token;
}
