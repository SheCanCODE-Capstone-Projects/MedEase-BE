package com.springboot.medease.Repository;

import com.springboot.medease.Models.Queue;
import com.springboot.medease.Models.QueueStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface QueueRepository extends MongoRepository<Queue, String> {

    Optional<Queue> findByPatientIdAndStatus(String patientId, QueueStatus status);

    // Return queues ordered by joinTime then id (deterministic)
    List<Queue> findByClinicIdAndServiceIdAndStatusOrderByJoinTimeAscIdAsc(
            String clinicId, String serviceId, QueueStatus status);
}
