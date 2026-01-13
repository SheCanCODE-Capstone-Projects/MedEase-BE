package com.springboot.medease.DTOs;

import com.springboot.medease.Models.QueueStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueuePositionDTO {
    private String queueId;
    private Integer position;
    private Integer totalWaiting;
    private QueueStatus status;
    private String clinicName;
    private String serviceName;
    private LocalDateTime joinTime;
    private String estimatedWaitTime;
    private String message;
}
