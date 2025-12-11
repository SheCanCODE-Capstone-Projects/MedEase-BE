package com.springboot.medease.Models;

import lombok.Data;

@Data
public class DoctorProfile extends Profile {
    private String doctorLicenseNumber;
    private String doctorSpecialization;
}
