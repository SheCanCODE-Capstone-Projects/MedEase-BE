package com.springboot.medease.Services;

import com.springboot.medease.DTOs.JoinQueueRequest;
import com.springboot.medease.DTOs.QueueResponseDTO;
import com.springboot.medease.Models.*;
import com.springboot.medease.Repository.ClinicRepository;
import com.springboot.medease.Repository.QueueRepository;
import com.springboot.medease.Repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QueueService {
    
    private final QueueRepository queueRepository;
    private final ClinicRepository clinicRepository;
    private final ServiceRepository serviceRepository;

    public QueueResponseDTO joinQueue(String patientId, JoinQueueRequest request) {

        if (queueRepository.findByPatientIdAndStatus(patientId, QueueStatus.WAITING).isPresent()) {
            throw new com.springboot.medease.GlobalException.QueueException("Patient already in queue");
        }

        // Create queue entry
        Queue queue = new Queue();
        queue.setPatientId(patientId);
        queue.setClinicId(request.getClinicId());
        queue.setServiceId(request.getServiceId());
        queue.setStatus(QueueStatus.WAITING);
        queue.setJoinTime(LocalDateTime.from(Instant.now()));

        queue = queueRepository.save(queue);
        queue.setQueuePosition(calculateQueuePosition(queue));
        queueRepository.save(queue);

        return mapToResponseDTO(queue);
    }

    public QueueResponseDTO getPatientQueue(String patientId) {
        Queue queue = queueRepository.findByPatientIdAndStatus(patientId, QueueStatus.WAITING)
                .orElseThrow(() -> new com.springboot.medease.GlobalException.QueueException("No active queue found"));
        return mapToResponseDTO(queue);
    }

    private int calculateQueuePosition(Queue queue) {
        long count = queueRepository.countByClinicIdAndServiceIdAndStatusAndJoinTimeBefore(
                queue.getClinicId(), queue.getServiceId(), QueueStatus.WAITING, queue.getJoinTime());
        return (int) count + 1;
    }

    public java.util.List<Clinic> getAllClinics() {
        return clinicRepository.findAll();
    }

    public java.util.List<com.springboot.medease.Models.Service> getServicesByClinic(String clinicId) {
        return serviceRepository.findByClinicId(clinicId);
    }

    private QueueResponseDTO mapToResponseDTO(Queue queue) {
        QueueResponseDTO dto = new QueueResponseDTO();
        dto.setQueueId(queue.getId());
        dto.setQueuePosition(queue.getQueuePosition());
        dto.setStatus(queue.getStatus());
        dto.setJoinTime(queue.getJoinTime());

        // Get clinic and service names
         Clinic clinic = clinicRepository.findById(queue.getClinicId())
                            .orElseThrow(() -> new IllegalStateException("Clinic not found: " + queue.getClinicId()));
         dto.setClinicName(clinic.getName());

                        com.springboot.medease.Models.Service service = serviceRepository.findById(queue.getServiceId())
                                .orElseThrow(() -> new IllegalStateException("Service not found: " + queue.getServiceId()));
               dto.setServiceName(service.getName());

        return dto;
    }
}