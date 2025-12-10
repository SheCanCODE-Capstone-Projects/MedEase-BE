package com.springboot.medease.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MedicalInfoUpdateRequest {
    @Size(max = 1000, message = "Chronic diseases must not exceed 1000 characters")
    private String chronicDiseases;
    @Size(max = 1000, message = "Medication allergies must not exceed 1000 characters")
    private String medicationAllergies;
}
