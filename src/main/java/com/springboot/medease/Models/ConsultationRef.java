package com.springboot.medease.Models;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ConsultationRef {
    private String consultationId;
    private LocalDateTime timestamp;
}
