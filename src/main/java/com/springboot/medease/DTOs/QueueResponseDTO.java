package com.springboot.medease.DTOs;

import com.springboot.medease.Models.QueueStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QueueResponseDTO {
    private String queueId;
    private int queuePosition;
    private QueueStatus status;
    private LocalDateTime joinTime;
    private String clinicName;
    private String serviceName;
}