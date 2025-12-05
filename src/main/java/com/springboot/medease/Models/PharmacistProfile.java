package com.springboot.medease.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PharmacistProfile {
    private String pharmacistFirstName;
    private String pharmacistLastName;
    private String pharmacistLicenseNumber;
    private String pharmacyName;
    private String pharmacyEmail;
}

