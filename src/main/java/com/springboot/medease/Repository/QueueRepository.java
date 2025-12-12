package com.springboot.medease.Repository;

import com.springboot.medease.Models.Queue;
import com.springboot.medease.Models.QueueStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface QueueRepository extends MongoRepository<Queue, String> {
    Optional<Queue> findByPatientIdAndStatus(String patientId, QueueStatus status);
    
    long countByClinicIdAndServiceIdAndStatusAndJoinTimeBefore(
        String clinicId, String serviceId, QueueStatus status, java.time.LocalDateTime joinTime);
    
    List<Queue> findByClinicIdAndServiceIdAndStatusOrderByJoinTime(
        String clinicId, String serviceId, QueueStatus status);
}