package com.springboot.medease.Services;

import com.springboot.medease.DTOs.QueueUpdateEvent;
import com.springboot.medease.Models.Clinic;
import com.springboot.medease.Models.QueueStatus;
import com.springboot.medease.Repository.ClinicRepository;
import com.springboot.medease.Repository.QueueRepository;
import com.springboot.medease.Repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class QueueEventPublisher {

    private static final EnumSet<QueueStatus> ACTIVE_STATUSES =
            EnumSet.of(QueueStatus.WAITING, QueueStatus.IN_PROGRESS);

    private final SimpMessagingTemplate messagingTemplate;
    private final QueueRepository queueRepository;
    private final ClinicRepository clinicRepository;
    private final ServiceRepository serviceRepository;

    /**
     * Publishes a queue update event to all subscribers
     */
    public void queueUpdated(String clinicId, String serviceId) {
        publishEvent(clinicId, serviceId, QueueUpdateEvent.EventType.POSITION_UPDATED, null);
    }

    /**
     * Publishes a patient joined event
     */
    public void patientJoined(String clinicId, String serviceId, String patientId) {
        publishEvent(clinicId, serviceId, QueueUpdateEvent.EventType.PATIENT_JOINED, patientId);
    }

    /**
     * Publishes a patient called event
     */
    public void patientCalled(String clinicId, String serviceId, String patientId) {
        publishEvent(clinicId, serviceId, QueueUpdateEvent.EventType.PATIENT_CALLED, patientId);
    }

    /**
     * Publishes a patient completed event
     */
    public void patientCompleted(String clinicId, String serviceId, String patientId) {
        publishEvent(clinicId, serviceId, QueueUpdateEvent.EventType.PATIENT_COMPLETED, patientId);
    }

    /**
     * Core method to publish queue events with detailed information
     */
    private void publishEvent(String clinicId, String serviceId,
                              QueueUpdateEvent.EventType eventType, String patientId) {
        try {
            // Get clinic and service names
            String clinicName = clinicRepository.findById(clinicId)
                    .map(Clinic::getName)
                    .orElse("Unknown Clinic");

            String serviceName = serviceRepository.findById(serviceId)
                    .map(com.springboot.medease.Models.Service::getName)
                    .orElse("Unknown Service");

            // Count waiting and in-progress patients
            long totalWaiting = queueRepository.countByClinicIdAndServiceIdAndStatus(
                    clinicId, serviceId, QueueStatus.WAITING);

            long totalInProgress = queueRepository.countByClinicIdAndServiceIdAndStatus(
                    clinicId, serviceId, QueueStatus.IN_PROGRESS);

            // Build the event
            QueueUpdateEvent event = QueueUpdateEvent.builder()
                    .eventType(eventType)
                    .clinicId(clinicId)
                    .serviceId(serviceId)
                    .clinicName(clinicName)
                    .serviceName(serviceName)
                    .totalWaiting((int) totalWaiting)
                    .totalInProgress((int) totalInProgress)
                    .patientId(patientId)
                    .timestamp(LocalDateTime.now())
                    .message(buildMessage(eventType, totalWaiting))
                    .build();

            // Send to WebSocket topic
            String destination = "/queue-updates/" + clinicId + "/" + serviceId;
            messagingTemplate.convertAndSend(destination, event);

        } catch (Exception e) {
            // Log error but don't fail the main operation
            System.err.println("Failed to publish queue event: " + e.getMessage());
        }
    }

    /**
     * Builds a user-friendly message based on event type
     */
    private String buildMessage(QueueUpdateEvent.EventType eventType, long totalWaiting) {
        switch (eventType) {
            case PATIENT_JOINED:
                return "A new patient joined the queue";
            case PATIENT_CALLED:
                return "Next patient has been called";
            case PATIENT_COMPLETED:
                return "Patient completed their appointment";
            case POSITION_UPDATED:
                return totalWaiting + " patient(s) waiting";
            default:
                return "Queue updated";
        }
    }
}