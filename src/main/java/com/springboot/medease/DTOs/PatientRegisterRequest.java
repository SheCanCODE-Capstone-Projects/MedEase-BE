package com.springboot.medease.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientRegisterRequest  extends RegisterRequest{
    private String insuranceProvider;
    private String insuranceNumber;
}
