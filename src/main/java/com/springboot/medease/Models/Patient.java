package com.springboot.medease.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "patients")
public class Patient extends PatientProfile {
    @Id
    private String id;

    private MedicalInfo medicalInfo;

}

