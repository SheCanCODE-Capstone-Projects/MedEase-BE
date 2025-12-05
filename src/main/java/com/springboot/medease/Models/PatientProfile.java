package com.springboot.medease.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientProfile {
    private String firstName;
    private String lastName;
    private String insuranceProvider;
    private String insuranceNumber;
}
