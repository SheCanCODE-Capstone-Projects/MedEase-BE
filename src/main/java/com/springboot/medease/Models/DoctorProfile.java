package com.springboot.medease.Models;

import lombok.Data;

@Data
public class DoctorProfile extends Profile {
    private String licenseNumber;
    private String specialization;
    private String doctorSpecialization;
    private String doctorLicenseNumber;
}
