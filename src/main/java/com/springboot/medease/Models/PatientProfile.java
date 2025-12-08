package com.springboot.medease.Models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientProfile extends Profile {

    @NotBlank(message = "name the insurance provider you use")
    @NotNull(message = "this field must not be null")
    private String insuranceProvider;
    @NotBlank(message = "provide the card number")
    @NotNull(message = "this field must not be null")
    private String insuranceNumber;
}
