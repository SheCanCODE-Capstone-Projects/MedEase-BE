package com.springboot.medease.DTOs;

import com.springboot.medease.Models.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientRegisterRequest  extends RegisterRequest{
    @NotBlank(message = "Insurance provider is required")
    @NotNull(message = "this field must not be null")
    private String insuranceProvider;

    @NotBlank(message = "Insurance number is required")
    @NotNull(message = "this field must not be null")
    private String insuranceNumber;


    @NotNull(message = "this field must not be null")
    private Date dateOfBirth;


    @NotNull(message = "this field must not be null")
    private Gender gender;

}
