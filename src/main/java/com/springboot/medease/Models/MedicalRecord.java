package com.springboot.medease.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicalRecord {

    private String name;
    private String diagnosingDoctorId;
    private LocalDate recordedAt;
}
