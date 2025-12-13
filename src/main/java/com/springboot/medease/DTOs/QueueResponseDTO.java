package com.springboot.medease.DTOs;

import com.springboot.medease.Models.QueueStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class QueueResponseDTO {
    private String queueId;
    private int queuePosition;
    private QueueStatus status;
    private Instant joinTime;
    private String clinicName;
    private String serviceName;
}