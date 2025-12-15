
package com.springboot.medease.Services;

import com.springboot.medease.DTOs.JoinQueueByNameRequest;
import com.springboot.medease.DTOs.JoinQueueRequest;
import com.springboot.medease.DTOs.QueueResponseDTO;
import com.springboot.medease.Models.*;
        import com.springboot.medease.Repository.ClinicRepository;
import com.springboot.medease.Repository.QueueRepository;
import com.springboot.medease.Repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        queue.setJoinTime(LocalDateTime.now());

        queue.setQueuePosition(calculateQueuePosition(queue));
        queue = queueRepository.save(queue);

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

    public QueueResponseDTO joinQueueByNames(String patientId, JoinQueueByNameRequest request) {

        String clinicName = request.getClinicName().trim();
        String serviceName = request.getServiceName().trim();

        List<Clinic> clinics = clinicRepository.findByNameIgnoreCase(clinicName);
        if (clinics.isEmpty()) {
            throw new com.springboot.medease.GlobalException.QueueException("Clinic not found: " + clinicName);
        }

        Clinic clinic;
        if (clinics.size() == 1) {
            clinic = clinics.getFirst();
        } else {
            String location = request.getClinicLocation() == null ? "" : request.getClinicLocation().trim();
            if (location.isEmpty()) {
                throw new com.springboot.medease.GlobalException.QueueException(
                        "Multiple clinics found with name '" + clinicName + "'. Please provide clinicLocation.");
            }

            clinic = clinics.stream()
                    .filter(c -> c.getLocation() != null && c.getLocation().equalsIgnoreCase(location))
                    .findFirst()
                    .orElseThrow(() -> new com.springboot.medease.GlobalException.QueueException(
                            "No clinic found with name '" + clinicName + "' at location '" + location + "'."));
        }

        com.springboot.medease.Models.Service service = serviceRepository
                .findByClinicIdAndNameIgnoreCase(clinic.getId(), serviceName)
                .orElseThrow(() -> new com.springboot.medease.GlobalException.QueueException(
                        "Service not found: " + serviceName + " for clinic " + clinic.getName()));

        JoinQueueRequest idRequest = new JoinQueueRequest();
        idRequest.setClinicId(clinic.getId());
        idRequest.setServiceId(service.getId());

        return joinQueue(patientId, idRequest);
    }
}