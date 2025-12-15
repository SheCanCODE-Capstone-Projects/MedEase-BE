package com.springboot.medease.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinQueueByNameRequest {

    @NotBlank
    private String clinicName;

    // Optional: only needed if multiple clinics share the same name
    private String clinicLocation;

    @NotBlank
    private String serviceName;
}