package com.springboot.medease.Models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientProfile extends Profile {

    @NotBlank(message = "Insurance provider is required")
    @NotNull(message = "this field must not be null")
    private String insuranceProvider;
    @NotBlank(message = "Insurance number is required")
    @NotNull(message = "this field must not be null")
    private String insuranceNumber;





}
