package com.springboot.medease.Controllers;

import com.springboot.medease.DTOs.JoinQueueByNameRequest;
import com.springboot.medease.DTOs.JoinQueueRequest;
import com.springboot.medease.DTOs.QueuePositionDTO;
import com.springboot.medease.DTOs.QueueResponseDTO;
import com.springboot.medease.Services.QueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient/queue")
@RequiredArgsConstructor
@Validated
public class QueueController {

    private final QueueService queueService;


    @PostMapping("/join")
    @PreAuthorize("hasRole('PATIENT')")
    public QueueResponseDTO joinQueue(@Valid @RequestBody JoinQueueRequest request,
                                      Authentication authentication) {
        return queueService.joinQueue(authentication.getName(), request);
    }


    @PostMapping("/join-by-names")
    @PreAuthorize("hasRole('PATIENT')")
    public QueueResponseDTO joinQueueByNames(@Valid @RequestBody JoinQueueByNameRequest request,
                                             Authentication authentication) {
        return queueService.joinQueueByNames(authentication.getName(), request);
    }
    @GetMapping("/status")
    @PreAuthorize("hasRole('PATIENT')")
    public QueueResponseDTO getQueueStatus(Authentication authentication) {
        return queueService.getPatientQueue(authentication.getName());
    }

    @GetMapping("/clinics")
    @PreAuthorize("hasRole('PATIENT')")
    public java.util.List<com.springboot.medease.Models.Clinic> getClinics() {
        return queueService.getAllClinics();
    }

    @GetMapping("/clinics/{clinicId}/services")
    @PreAuthorize("hasRole('PATIENT')")
    public java.util.List<com.springboot.medease.Models.Service> getClinicServices(@PathVariable String clinicId) {
        return queueService.getServicesByClinic(clinicId);
    }

    /**
     * Get real-time queue position for the patient
     * This endpoint provides detailed position information including estimated wait time
     */
    @GetMapping("/my-position")
    @PreAuthorize("hasRole('PATIENT')")
    public QueuePositionDTO getMyPosition(Authentication authentication) {
        return queueService.getPatientPosition(authentication.getName());
    }
}