package com.springboot.medease.Controllers;

import com.springboot.medease.DTOs.QueueResponseDTO;
import com.springboot.medease.Services.QueueService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor/queue")
@RequiredArgsConstructor
@Validated
public class DoctorController {

    private final QueueService queueService;

    /**
     * Doctor calls the next patient in the queue
     * This will automatically send WebSocket updates to all waiting patients
     */
    @PostMapping("/call-next")
    @PreAuthorize("hasRole('DOCTOR')")
    public QueueResponseDTO callNextPatient(
            @RequestParam @NotBlank(message = "Clinic ID is required") String clinicId,
            @RequestParam @NotBlank(message = "Service ID is required") String serviceId,
            Authentication authentication) {
        String doctorId = authentication.getName();
        return queueService.callNextPatient(doctorId, clinicId, serviceId);
    }

    /**
     * Get all waiting patients for a specific clinic and service
     */
    @GetMapping("/waiting-patients")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<QueueResponseDTO> getWaitingPatients(
            @RequestParam @NotBlank(message = "Clinic ID is required") String clinicId,
            @RequestParam @NotBlank(message = "Service ID is required") String serviceId) {
        return queueService.getWaitingPatients(clinicId, serviceId);
    }

    /**
     * Get current patient being served by this doctor
     */
    @GetMapping("/current-patient")
    @PreAuthorize("hasRole('DOCTOR')")
    public QueueResponseDTO getCurrentPatient(Authentication authentication) {
        String doctorId = authentication.getName();
        return queueService.getCurrentPatientForDoctor(doctorId);
    }

    /**
     * Mark current patient as completed
     */
    @PostMapping("/complete-patient")
    @PreAuthorize("hasRole('DOCTOR')")
    public void completePatient(
            @RequestParam @NotBlank(message = "Queue ID is required") String queueId,
            @RequestParam @NotBlank(message = "Clinic ID is required") String clinicId,
            @RequestParam @NotBlank(message = "Service ID is required") String serviceId,
            Authentication authentication) {
        String doctorId = authentication.getName();
        queueService.completePatient(queueId, doctorId, clinicId, serviceId);
    }
}
