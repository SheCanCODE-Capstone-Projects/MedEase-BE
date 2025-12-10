package com.springboot.medease.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PatientUpdateRequest {



        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;

        @NotBlank(message = "Phone number is required")
        private String phoneNumber;

        @NotNull(message = "Date of birth is required")
        private Date dateOfBirth;

        @NotNull(message = "Gender is required")
        private String gender;

        private String insuranceProvider;
        private String insuranceNumber;
    }

