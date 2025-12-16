package com.springboot.medease.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinQueueRequest {
    @NotBlank
    private String clinicId;
    
    @NotBlank
    private String serviceId;
}