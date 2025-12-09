package com.springboot.medease.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Patient {
    @Id
    private String id;


    private PersonalInfo personalInfo;

    private MedicalInfo medicalInfo;

}

