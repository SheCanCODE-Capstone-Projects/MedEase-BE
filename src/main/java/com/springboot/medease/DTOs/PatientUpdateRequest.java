package com.springboot.medease.DTOs;

import com.springboot.medease.Models.Gender;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PatientUpdateRequest {



        private String firstName;
        private String lastName;
        @Email(message = "Email must be valid")
        private String email;
        private String phoneNumber;
        private Date dateOfBirth;
        private Gender gender;

        private String insuranceProvider;
        private String insuranceNumber;
        private String subjective;
    }

