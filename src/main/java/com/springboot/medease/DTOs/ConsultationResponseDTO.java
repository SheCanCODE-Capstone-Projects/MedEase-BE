package com.springboot.medease.DTOs;

import lombok.Data;

import java.time.Instant;
@Data
public class ConsultationResponseDTO {

    private String id;
    private String diagnosis;
    private String symptoms;
    private String doctorId;
    private String patientId;
    private String clinicId;
    private Instant timestamp;
}
