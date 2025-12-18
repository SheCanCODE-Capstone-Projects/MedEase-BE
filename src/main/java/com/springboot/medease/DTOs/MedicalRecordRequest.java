package com.springboot.medease.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MedicalRecordRequest {

    @NotBlank
    private String name;
}
