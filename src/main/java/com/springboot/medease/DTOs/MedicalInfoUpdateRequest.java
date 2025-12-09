package com.springboot.medease.DTOs;

import lombok.Data;

@Data
public class MedicalInfoUpdateRequest {
    private String chronicDiseases;
    private String medicationAllergies;
}
