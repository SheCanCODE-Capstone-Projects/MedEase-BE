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
public class PharmacistRegisterRequest extends RegisterRequest{

    @NotBlank(message = "License number of Pharmacist is required")
    private String pharmacistLicenseNumber;

    @NotBlank(message = "Pharmacy name is required")
    private String pharmacyName;

}

