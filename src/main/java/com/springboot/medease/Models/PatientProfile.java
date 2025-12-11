package com.springboot.medease.Models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientProfile extends Profile {

    @Field("profilePatientReference")
    private String patientReference;
    
    @NotBlank(message = "Insurance provider is required")
    @NotNull(message = "this field must not be null")
    private String insuranceProvider;
    @NotBlank(message = "Insurance number is required")
    @NotNull(message = "this field must not be null")
    private String insuranceNumber;





}
