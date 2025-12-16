package com.springboot.medease.Repository;

import com.springboot.medease.Models.Queue;
import com.springboot.medease.Models.QueueStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface QueueRepository extends MongoRepository<Queue, String> {

    Optional<Queue> findByPatientIdAndStatus(String patientId, QueueStatus status);

    Optional<Queue> findFirstByPatientIdAndStatusIn(String patientId, Collection<QueueStatus> statuses);

    long countByClinicIdAndServiceIdAndStatusAndJoinTimeBefore(
            String clinicId, String serviceId, QueueStatus status, LocalDateTime joinTime);

    long countByClinicIdAndServiceIdAndStatusInAndJoinTimeBefore(
            String clinicId, String serviceId, Collection<QueueStatus> statuses, LocalDateTime joinTime);

    List<Queue> findByClinicIdAndServiceIdAndStatusOrderByJoinTime(
            String clinicId, String serviceId, QueueStatus status);

    long countByClinicIdAndServiceIdAndStatus(
            String clinicId, String serviceId, QueueStatus status);

    Optional<Queue> findByAssignedDoctorIdAndStatus(String doctorId, QueueStatus status);
}