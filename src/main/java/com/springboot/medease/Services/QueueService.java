package com.springboot.medease.Services;

import com.springboot.medease.DTOs.JoinQueueByNameRequest;
import com.springboot.medease.DTOs.JoinQueueRequest;
import com.springboot.medease.DTOs.QueueResponseDTO;
import com.springboot.medease.GlobalException.QueueConflictException;
import com.springboot.medease.GlobalException.QueueException;
import com.springboot.medease.Models.Clinic;
import com.springboot.medease.Models.Queue;
import com.springboot.medease.Models.QueueStatus;
import com.springboot.medease.Repository.ClinicRepository;
import com.springboot.medease.Repository.QueueRepository;
import com.springboot.medease.Repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueService {

    private static final EnumSet<QueueStatus> ACTIVE_STATUSES =
            EnumSet.of(QueueStatus.WAITING, QueueStatus.IN_PROGRESS);

    private final QueueRepository queueRepository;
    private final ClinicRepository clinicRepository;
    private final ServiceRepository serviceRepository;

    /**
     * Patient selects clinic & service and joins the queue.
     *
     * Concurrency:
     * - App-level pre-check gives a friendly message.
     * - DB partial unique index (ux_patient_one_active_queue) is the final guard against race conditions.
     */
    public QueueResponseDTO joinQueue(String patientId, JoinQueueRequest request) {
        String clinicId = request.getClinicId();
        String serviceId = request.getServiceId();

        // Validate clinic exists
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new QueueException("Clinic not found: " + clinicId));

        // Validate service exists and belongs to clinic (recommended)
        com.springboot.medease.Models.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new QueueException("Service not found: " + serviceId));
        if (!clinicId.equals(service.getClinicId())) {
            throw new QueueException("Service does not belong to the selected clinic");
        }

        // Friendly pre-check
        if (queueRepository.findFirstByPatientIdAndStatusIn(patientId, ACTIVE_STATUSES).isPresent()) {
            throw new QueueException("Patient already has an active queue entry");
        }

        Queue queue = new Queue();
        queue.setPatientId(patientId);
        queue.setClinicId(clinicId);
        queue.setServiceId(serviceId);
        queue.setStatus(QueueStatus.WAITING);
        queue.setJoinTime(LocalDateTime.now());
        queue.setAssignedDoctorId(null);

        // queuePosition is derived; keep stored value but don't treat it as the source of truth
        queue.setQueuePosition(0);

        try {
            queue = queueRepository.save(queue);
        } catch (DuplicateKeyException ex) {
            // Race condition: another request inserted an active queue for same patientId
            throw new QueueConflictException("Patient already has an active queue entry");
        }

        return mapToResponseDTO(queue, clinic, service);
    }

    public QueueResponseDTO getPatientQueue(String patientId) {
        Queue queue = queueRepository.findFirstByPatientIdAndStatusIn(patientId, ACTIVE_STATUSES)
                .orElseThrow(() -> new QueueException("No active queue found"));

        // Load related entities for response fields
        Clinic clinic = clinicRepository.findById(queue.getClinicId())
                .orElseThrow(() -> new IllegalStateException("Clinic not found: " + queue.getClinicId()));
        com.springboot.medease.Models.Service service = serviceRepository.findById(queue.getServiceId())
                .orElseThrow(() -> new IllegalStateException("Service not found: " + queue.getServiceId()));

        return mapToResponseDTO(queue, clinic, service);
    }

    public List<Clinic> getAllClinics() {
        return clinicRepository.findAll();
    }

    public List<com.springboot.medease.Models.Service> getServicesByClinic(String clinicId) {
        return serviceRepository.findByClinicId(clinicId);
    }

    public QueueResponseDTO joinQueueByNames(String patientId, JoinQueueByNameRequest request) {
        String clinicName = request.getClinicName().trim();
        String serviceName = request.getServiceName().trim();

        List<Clinic> clinics = clinicRepository.findByNameIgnoreCase(clinicName);
        if (clinics.isEmpty()) {
            throw new QueueException("Clinic not found: " + clinicName);
        }

        Clinic clinic;
        if (clinics.size() == 1) {
            clinic = clinics.get(0);
        } else {
            String location = request.getClinicLocation() == null ? "" : request.getClinicLocation().trim();
            if (location.isEmpty()) {
                throw new QueueException(
                        "Multiple clinics found with name '" + clinicName + "'. Please provide clinicLocation."
                );
            }

            clinic = clinics.stream()
                    .filter(c -> c.getLocation() != null && c.getLocation().equalsIgnoreCase(location))
                    .findFirst()
                    .orElseThrow(() -> new QueueException(
                            "No clinic found with name '" + clinicName + "' at location '" + location + "'."
                    ));
        }

        com.springboot.medease.Models.Service service = serviceRepository
                .findByClinicIdAndNameIgnoreCase(clinic.getId(), serviceName)
                .orElseThrow(() -> new QueueException(
                        "Service not found: " + serviceName + " for clinic " + clinic.getName()
                ));

        JoinQueueRequest idRequest = new JoinQueueRequest();
        idRequest.setClinicId(clinic.getId());
        idRequest.setServiceId(service.getId());

        return joinQueue(patientId, idRequest);
    }

    private QueueResponseDTO mapToResponseDTO(
            Queue queue,
            Clinic clinic,
            com.springboot.medease.Models.Service service
    ) {
        QueueResponseDTO dto = new QueueResponseDTO();
        dto.setQueueId(queue.getId());
        dto.setStatus(queue.getStatus());
        dto.setJoinTime(queue.getJoinTime());
        dto.setAssignedDoctorId(queue.getAssignedDoctorId());

        // Positions are derived from joinTime + status:
        // - waitingPosition ignores IN_PROGRESS (your “shift” rule)
        // - overallActivePosition counts WAITING + IN_PROGRESS
        Integer waitingPosition = null;
        if (queue.getStatus() == QueueStatus.WAITING) {
            long waitingCountAhead = queueRepository.countByClinicIdAndServiceIdAndStatusAndJoinTimeBefore(
                    queue.getClinicId(), queue.getServiceId(), QueueStatus.WAITING, queue.getJoinTime()
            );
            waitingPosition = Math.toIntExact(waitingCountAhead + 1);
        }

        long activeCountAhead = queueRepository.countByClinicIdAndServiceIdAndStatusInAndJoinTimeBefore(
                queue.getClinicId(), queue.getServiceId(), ACTIVE_STATUSES, queue.getJoinTime()
        );
        int overallActivePosition = Math.toIntExact(activeCountAhead + 1);

        dto.setWaitingPosition(waitingPosition);
        dto.setOverallActivePosition(overallActivePosition);

        // Keep queuePosition field aligned with WAITING position for now (compatibility)
        dto.setQueuePosition(waitingPosition != null ? waitingPosition : 0);

        dto.setClinicName(clinic.getName());
        dto.setServiceName(service.getName());

        return dto;
    }
}