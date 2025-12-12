package com.springboot.medease.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "User")
public class User {
    @Id
    private String id;
    private List<PatientProfile> patients;
    private List<DoctorProfile> doctors;
    private List<PharmacistProfile> pharmacists;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
