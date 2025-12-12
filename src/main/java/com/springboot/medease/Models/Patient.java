package com.springboot.medease.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document(collection = "patients")
public class Patient extends PatientProfile {
    @Id
    private String id;

    @Field("medicalInfo")
    private MedicalInfo medicalInfo;

    @Field("patientRef")
    @org.springframework.data.mongodb.core.index.Indexed(unique = true)
    private String patientReference;


}

