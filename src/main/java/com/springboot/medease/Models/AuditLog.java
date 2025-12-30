package com.springboot.medease.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    private String id;

    private String action;
    private String performedBy;
    private String patientId;
    private LocalDateTime timestamp;

    public AuditLog(String action, String performedBy, String patientId, LocalDateTime timestamp) {
        this.action = action;
        this.performedBy = performedBy;
        this.patientId = patientId;
        this.timestamp = timestamp;
    }
}
