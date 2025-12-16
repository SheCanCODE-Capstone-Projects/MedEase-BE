package com.springboot.medease.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConsultationRequestDTO {

    @NotBlank
    private String diagnosis;

    @NotBlank
    private String symptoms;

    @NotBlank
    private String doctorId;

    @NotBlank
    private String patientId;

    @NotBlank
    private String clinicId;

}
