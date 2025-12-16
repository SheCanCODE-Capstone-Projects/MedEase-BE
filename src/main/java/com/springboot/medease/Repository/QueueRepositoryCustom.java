package com.springboot.medease.Repository;

import com.springboot.medease.Models.Queue;
import com.springboot.medease.Models.QueueStatus;

import java.util.Optional;

public interface QueueRepositoryCustom {
    /**
     * Atomically finds and locks the next waiting patient for a clinic/service.
     * This prevents race conditions when multiple doctors call the next patient simultaneously.
     *
     * @param clinicId The clinic ID
     * @param serviceId The service ID
     * @param doctorId The doctor ID to assign
     * @return Optional containing the locked queue entry, or empty if no patients waiting
     */
    Optional<Queue> findAndAssignNextPatient(String clinicId, String serviceId, String doctorId);
}
