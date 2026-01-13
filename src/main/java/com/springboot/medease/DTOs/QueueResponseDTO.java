package com.springboot.medease.DTOs;

import com.springboot.medease.Models.QueueStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QueueResponseDTO {
    private String queueId;

    /**
     * Kept for backward compatibility with your existing API.
     * Going forward, prefer waitingPosition / overallActivePosition.
     */
    private int queuePosition;

    private Integer waitingPosition;
    private Integer overallActivePosition;

    private QueueStatus status;
    private LocalDateTime joinTime;

    private String clinicName;
    private String serviceName;

    private String assignedDoctorId;
}