package com.springboot.medease.DTOs;

import com.springboot.medease.Models.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PatientUpdateRequest {



        @Size(max = 50, message = "First name must not exceed 50 characters")
        private String firstName;

        @Size(max = 50, message = "Last name must not exceed 50 characters")
        private String lastName;

        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        private String email;

        @Size(max = 15, message = "Phone number must not exceed 15 characters")
        private String phoneNumber;

        private Date dateOfBirth;

        private Gender gender;

        @Size(max = 100, message = "Insurance provider must not exceed 100 characters")
        private String insuranceProvider;

        @Size(max = 50, message = "Insurance number must not exceed 50 characters")
        private String insuranceNumber;

        @Size(max = 1001, message = "Subjective must not exceed 1000 characters")
        private String subjective;
    }

