package com.springboot.medease.DTOs;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ChronicConditionResponseDTO {

    private String name;
    private String type;
    private String diagnosedByDoctorId;
    private LocalDate diagnosedDate;
}
