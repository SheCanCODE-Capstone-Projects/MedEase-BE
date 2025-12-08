package com.springboot.medease.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "User")
public class User {

    @Id
    private String id;

    private UserType userType;

    private List<PatientProfile> patients = new ArrayList<>();
    private List<DoctorProfile> doctors = new ArrayList<>();
    private List<PharmacistProfile> pharmacists = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}

