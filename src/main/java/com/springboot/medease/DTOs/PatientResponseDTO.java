package com.springboot.medease.DTOs;

import com.springboot.medease.Models.Gender;
import com.springboot.medease.Models.MedicalInfo;
import lombok.Data;

import java.util.Date;

@Data
public class PatientResponseDTO {
    private String id;
    private String firstName;
    private String lastName;

    private String email;
    private String phoneNumber;
    private Date dateOfBirth;
    private Gender gender;
    private MedicalInfo medicalInfo;
    private String subjective;
}
