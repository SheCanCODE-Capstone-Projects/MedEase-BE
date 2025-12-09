package com.springboot.medease.Models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PharmacistProfile extends Profile {

    private String pharmacistLicenseNumber;

    @NotBlank(message = "provide the pharmacy name ")
    private String pharmacyName;

}

