package com.springboot.medease.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for real-time queue update events sent via WebSocket
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueUpdateEvent {

    /**
     * Type of queue event
     */
    private EventType eventType;

    /**
     * Clinic ID where the queue update occurred
     */
    private String clinicId;

    /**
     * Service ID for the queue
     */
    private String serviceId;

    /**
     * Clinic name for display
     */
    private String clinicName;

    /**
     * Service name for display
     */
    private String serviceName;

    /**
     * Total number of patients waiting in queue
     */
    private Integer totalWaiting;

    /**
     * Total number of patients being served
     */
    private Integer totalInProgress;

    /**
     * Patient ID if this event is specific to a patient
     */
    private String patientId;

    /**
     * New position for the patient (if applicable)
     */
    private Integer newPosition;

    /**
     * Timestamp of the event
     */
    private LocalDateTime timestamp;

    /**
     * Additional message for the event
     */
    private String message;

    /**
     * Event types for queue updates
     */
    public enum EventType {
        PATIENT_JOINED,      // New patient joined the queue
        PATIENT_CALLED,      // Patient was called by doctor
        PATIENT_COMPLETED,   // Patient completed their appointment
        PATIENT_LEFT,        // Patient left the queue
        POSITION_UPDATED,    // Queue positions updated
        QUEUE_CLEARED        // Queue was cleared
    }
}