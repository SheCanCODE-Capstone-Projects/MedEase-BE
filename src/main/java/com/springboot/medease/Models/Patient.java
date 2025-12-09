package com.springboot.medease.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Patient extends Profile {
    @Id
    private String id;

    private MedicalInfo medicalInfo;

}

