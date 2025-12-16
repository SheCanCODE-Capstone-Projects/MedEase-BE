package com.springboot.medease.Services;

import com.springboot.medease.DTOs.JoinQueueByNameRequest;
import com.springboot.medease.DTOs.JoinQueueRequest;
import com.springboot.medease.DTOs.QueueResponseDTO;
import com.springboot.medease.GlobalException.QueueConflictException;
import com.springboot.medease.GlobalException.QueueException;
import com.springboot.medease.Models.Clinic;
import com.springboot.medease.Models.Queue;
import com.springboot.medease.Models.QueueStatus;
import com.springboot.medease.Models.Service;
import com.springboot.medease.Repository.ClinicRepository;
import com.springboot.medease.Repository.QueueRepository;
import com.springboot.medease.Repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class QueueService {

    private static final EnumSet<QueueStatus> ACTIVE_STATUSES =
            EnumSet.of(QueueStatus.WAITING, QueueStatus.IN_PROGRESS);

    private final QueueRepository queueRepository;
    private final ClinicRepository clinicRepository;
    private final ServiceRepository serviceRepository;
    private final QueueEventPublisher queueEventPublisher;



    /**
     * Patient joins a queue for a specific clinic + service.
     *
     * Concurrency notes:
     * - We do a friendly pre-check to return a clean message in the normal case.
     * - The DB unique partial index (ux_patient_one_active_queue) is the final guard against race conditions.
     */
    @Transactional
    public QueueResponseDTO joinQueue(String patientId, JoinQueueRequest request) {
        String clinicId = request.getClinicId();
        String serviceId = request.getServiceId();

        // Validate clinic exists
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new QueueException("Clinic not found: " + clinicId));

        // Validate service exists and belongs to that clinic
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new QueueException("Service not found: " + serviceId));
        if (service.getClinicId() == null || !clinicId.equals(service.getClinicId())) {
            throw new QueueException("Service does not belong to the selected clinic");
        }

        // Enforce: patient can have ONLY ONE active queue entry globally (WAITING or IN_PROGRESS)
        if (queueRepository.findFirstByPatientIdAndStatusIn(patientId, ACTIVE_STATUSES).isPresent()) {
            throw new QueueConflictException("Patient already has an active queue entry");
        }

        Queue queue = new Queue();
        queue.setPatientId(patientId);
        queue.setClinicId(clinicId);
        queue.setServiceId(serviceId);
        queue.setStatus(QueueStatus.WAITING);
        queue.setJoinTime(LocalDateTime.now());
        queue.setAssignedDoctorId(null);

        // Derived in API response; keep stored value for compatibility/auditing if needed
        queue.setQueuePosition(0);

        try {
            queue = queueRepository.save(queue);

            // ADD THIS LINE: Notify all patients that someone joined
            queueEventPublisher.patientJoined(clinicId, serviceId, patientId);

        } catch (DuplicateKeyException ex) {
            // Race condition: another request inserted an active queue for same patientId
            throw new QueueConflictException("Patient already has an active queue entry");
        }



        return mapToResponseDTO(queue, clinic, service);
    }




    /**
     * Returns the patient's active queue entry (WAITING or IN_PROGRESS).
     *
     * Note: We intentionally throw QueueException for missing clinic/service references to keep
     * API behavior consistent with joinQueue() (client receives a 4xx rather than a 500).
     */
    public QueueResponseDTO getPatientQueue(String patientId) {
        Queue queue = queueRepository.findFirstByPatientIdAndStatusIn(patientId, ACTIVE_STATUSES)
                .orElseThrow(() -> new QueueException("No active queue found"));

        Clinic clinic = clinicRepository.findById(queue.getClinicId())
                .orElseThrow(() -> new QueueException("Clinic not found: " + queue.getClinicId()));
        Service service = serviceRepository.findById(queue.getServiceId())
                .orElseThrow(() -> new QueueException("Service not found: " + queue.getServiceId()));

        return mapToResponseDTO(queue, clinic, service);
    }

    public List<Clinic> getAllClinics() {
        return clinicRepository.findAll();
    }

    public List<Service> getServicesByClinic(String clinicId) {
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

        Service service = serviceRepository
                .findByClinicIdAndNameIgnoreCase(clinic.getId(), serviceName)
                .orElseThrow(() -> new QueueException(
                        "Service not found: " + serviceName + " for clinic " + clinic.getName()
                ));

        JoinQueueRequest idRequest = new JoinQueueRequest();
        idRequest.setClinicId(clinic.getId());
        idRequest.setServiceId(service.getId());

        return joinQueue(patientId, idRequest);
    }

    @Transactional
    public QueueResponseDTO callNextPatient(String doctorId, String clinicId, String serviceId) {

        Queue next = queueRepository
                .findByClinicIdAndServiceIdAndStatusOrderByJoinTime(clinicId, serviceId, QueueStatus.WAITING)
                .stream()
                .findFirst()
                .orElseThrow(() -> new QueueException("No patients waiting"));

        next.setStatus(QueueStatus.IN_PROGRESS);
        next.setAssignedDoctorId(doctorId);

        queueRepository.save(next);

        // Notify all waiting patients in real-time
        queueEventPublisher.patientCalled(clinicId, serviceId, next.getPatientId());


        return mapToResponseDTO(next,
                clinicRepository.findById(clinicId)
                    .orElseThrow(() -> new QueueException("Clinic not found: " + clinicId)),
                serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new QueueException("Service not found: " + serviceId))
        );
    }

    /**
     * Get all waiting patients for a clinic and service
     */
    public List<QueueResponseDTO> getWaitingPatients(String clinicId, String serviceId) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new QueueException("Clinic not found"));
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new QueueException("Service not found"));

        List<Queue> waitingQueues = queueRepository
                .findByClinicIdAndServiceIdAndStatusOrderByJoinTime(clinicId, serviceId, QueueStatus.WAITING);

        return waitingQueues.stream()
                .map(queue -> mapToResponseDTO(queue, clinic, service))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get current patient being served by a doctor
     */
    public QueueResponseDTO getCurrentPatientForDoctor(String doctorId) {
        Queue queue = queueRepository.findByAssignedDoctorIdAndStatus(doctorId, QueueStatus.IN_PROGRESS)
                .orElseThrow(() -> new QueueException("No patient currently being served"));

        Clinic clinic = clinicRepository.findById(queue.getClinicId())
                .orElseThrow(() -> new QueueException("Clinic not found"));
        Service service = serviceRepository.findById(queue.getServiceId())
                .orElseThrow(() -> new QueueException("Service not found"));

        return mapToResponseDTO(queue, clinic, service);
    }

    /**
     * Complete a patient's appointment
     * This method is transactional to ensure atomicity
     */
    @Transactional
    public void completePatient(String queueId, String doctorId, String clinicId, String serviceId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new QueueException("Queue entry not found"));

        if (!clinicId.equals(queue.getClinicId()) || !serviceId.equals(queue.getServiceId())) {
            throw new QueueException("Clinic or service mismatch for this queue entry");
        }

        if (!doctorId.equals(queue.getAssignedDoctorId())) {
            throw new QueueException("This patient is not assigned to you");
        }

        if (queue.getStatus() != QueueStatus.IN_PROGRESS) {
            throw new QueueException("Patient is not currently in progress");
        }

        queue.setStatus(QueueStatus.COMPLETED);
        queueRepository.save(queue);

        // Notify all waiting patients that someone completed
        queueEventPublisher.patientCompleted(queue.getClinicId(), queue.getServiceId(), queue.getPatientId());
    }

    /**
     * Get patient's current position in queue with additional context
     */
    public com.springboot.medease.DTOs.QueuePositionDTO getPatientPosition(String patientId) {
        Queue queue = queueRepository.findFirstByPatientIdAndStatusIn(patientId, ACTIVE_STATUSES)
                .orElseThrow(() -> new QueueException("No active queue found"));

        Clinic clinic = clinicRepository.findById(queue.getClinicId())
                .orElseThrow(() -> new QueueException("Clinic not found"));
        Service service = serviceRepository.findById(queue.getServiceId())
                .orElseThrow(() -> new QueueException("Service not found"));

        Integer position = null;
        long totalWaiting = queueRepository.countByClinicIdAndServiceIdAndStatus(
                queue.getClinicId(), queue.getServiceId(), QueueStatus.WAITING);

        String message;
        if (queue.getStatus() == QueueStatus.WAITING) {
            long waitingCountAhead = queueRepository.countByClinicIdAndServiceIdAndStatusAndJoinTimeBefore(
                    queue.getClinicId(), queue.getServiceId(), QueueStatus.WAITING, queue.getJoinTime()
            );
            position = Math.toIntExact(waitingCountAhead + 1);
            message = position == 1 ? "You're next!" : position + " patient(s) ahead of you";
        } else if (queue.getStatus() == QueueStatus.IN_PROGRESS) {
            message = "You are currently being served";
        } else {
            message = "Queue status: " + queue.getStatus();
        }

        return com.springboot.medease.DTOs.QueuePositionDTO.builder()
                .queueId(queue.getId())
                .position(position)
                .totalWaiting((int) totalWaiting)
                .status(queue.getStatus())
                .clinicName(clinic.getName())
                .serviceName(service.getName())
                .joinTime(queue.getJoinTime())
                .estimatedWaitTime(calculateEstimatedWaitTime(position))
                .message(message)
                .build();
    }

    /**
     * Calculate estimated wait time (simple implementation)
     * You can enhance this based on historical data
     */
    private String calculateEstimatedWaitTime(Integer position) {
        if (position == null || position <= 0) {
            return "N/A";
        }
        // Assume 10 minutes per patient (adjust based on your needs)
        int minutes = position * 10;
        if (minutes < 60) {
            return minutes + " minutes";
        } else {
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;
            return hours + "h " + remainingMinutes + "m";
        }
    }

    private QueueResponseDTO mapToResponseDTO(Queue queue, Clinic clinic, Service service) {
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