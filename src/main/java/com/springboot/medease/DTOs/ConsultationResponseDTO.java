package com.springboot.medease.DTOs;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ConsultationResponseDTO {

    private String id;
    private String diagnosis;
    private String symptoms;
    private String doctorId;
    private String patientId;
    private String clinicId;
    private LocalDateTime timestamp;
}
