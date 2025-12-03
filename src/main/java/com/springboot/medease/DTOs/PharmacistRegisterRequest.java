package com.springboot.medease.DTOs;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PharmacistRegisterRequest {

    @NotBlank(message = "Pharmacist First name is required")
    private String pharmacistFirstName;

    @NotBlank(message = "Pharmacist Last name is required")
    private String pharmacistLastName;

    @NotBlank(message = "License number of Pharmacist is required")
    private String pharmacistLicenseNumber;

    @NotBlank(message = "phoneNumber is required")
    @Indexed(unique = true)
    private String phoneNumber;

    @NotBlank(message = "Pharmacy name is required")
    private String pharmacyName;

    @NotBlank(message = "Pharmacy email is required")
    @Email
    private String pharmacyEmail;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}

