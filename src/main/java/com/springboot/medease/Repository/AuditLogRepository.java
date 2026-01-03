package com.springboot.medease.Repository;

import com.springboot.medease.Models.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditLogRepository  extends MongoRepository<AuditLog, String> {

    List<AuditLog> findByPatientId(String patientId);
    List<AuditLog> findByAction(String action);
    List<AuditLog> findByPerformedBy(String doctorId);

}
