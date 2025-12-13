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
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueService {
    
    private final QueueRepository queueRepository;
    private final ClinicRepository clinicRepository;
    private final ServiceRepository serviceRepository;

    public QueueResponseDTO joinQueue(String patientId, JoinQueueRequest request) {
        // 1️⃣ Check if patient is already in queue
        if (queueRepository.findByPatientIdAndStatus(patientId, QueueStatus.WAITING).isPresent()) {
            throw new com.springboot.medease.GlobalException.QueueException("Patient already in queue");
        }

        // 2️⃣ Create new queue entry
        Queue queue = new Queue();
        queue.setPatientId(patientId);
        queue.setClinicId(request.getClinicId());
        queue.setServiceId(request.getServiceId());
        queue.setStatus(QueueStatus.WAITING);
        queue.setJoinTime(Instant.now());

        // Save to get generated ID
        queue = queueRepository.save(queue);

        // 3️⃣ Determine position deterministically (joinTime + id as tiebreaker)
        List<Queue> queues = queueRepository
                .findByClinicIdAndServiceIdAndStatusOrderByJoinTimeAscIdAsc(
                        queue.getClinicId(), queue.getServiceId(), QueueStatus.WAITING);

        int position = 1;
        for (Queue q : queues) {
            if (q.getId().equals(queue.getId())) break;
            position++;
        }
        queue.setQueuePosition(position);

        // 4️⃣ Save updated position
        queueRepository.save(queue);

        // 5️⃣ Map to response DTO
        return mapToResponseDTO(queue);
    }


    public QueueResponseDTO getPatientQueue(String patientId) {
        Queue queue = queueRepository.findByPatientIdAndStatus(patientId, QueueStatus.WAITING)
                .orElseThrow(() -> new com.springboot.medease.GlobalException.QueueException("No active queue found"));
        return mapToResponseDTO(queue);
    }

    private int calculateQueuePosition(Queue queue) {
        List<Queue> queues = queueRepository
                .findByClinicIdAndServiceIdAndStatusOrderByJoinTimeAscIdAsc(
                        queue.getClinicId(), queue.getServiceId(), QueueStatus.WAITING);

        int position = 1;
        for (Queue q : queues) {
            if (q.getId().equals(queue.getId())) break;
            position++;
        }
        return position;
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
        clinicRepository.findById(queue.getClinicId())
                .ifPresent(clinic -> dto.setClinicName(clinic.getName()));
        serviceRepository.findById(queue.getServiceId())
                .ifPresent(service -> dto.setServiceName(service.getName()));
        
        return dto;
    }
}