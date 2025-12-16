package com.springboot.medease.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ConsultationRequestDTO {

    @NotBlank(message = "Diagnosis is required")
    @Size(min = 3, max = 500, message = "Diagnosis must be between 3 and 500 characters")
    private String diagnosis;

    @NotBlank(message = "Symptoms are required")
    @Size(min = 3, max = 1000, message = "Symptoms must be between 3 and 1000 characters")
    private String symptoms;

    @NotBlank(message = "Doctor ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Doctor ID must contain only alphanumeric characters")
    private String doctorId;

    @NotBlank(message = "Patient ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Patient ID must contain only alphanumeric characters")
    private String patientId;

    @NotBlank(message = "Clinic ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Clinic ID must contain only alphanumeric characters")
    private String clinicId;

}
