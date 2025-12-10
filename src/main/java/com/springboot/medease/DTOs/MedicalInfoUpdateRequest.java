package com.springboot.medease.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MedicalInfoUpdateRequest {
    @NotBlank(message = "Chronic diseases field is required")
    private String chronicDiseases;
    @NotBlank(message = "Medication allergies field is required")
    private String medicationAllergies;
}
