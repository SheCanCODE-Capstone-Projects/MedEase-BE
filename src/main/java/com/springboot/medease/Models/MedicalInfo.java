package com.springboot.medease.Models;

import lombok.Data;

@Data
public class MedicalInfo {
    private String chronicDiseases;
    private String medicationAllergies;
    private String updatedByDoctorId;
}
