package com.springboot.medease.Controllers;

import com.springboot.medease.DTOs.JoinQueueRequest;
import com.springboot.medease.DTOs.QueueResponseDTO;
import com.springboot.medease.Services.QueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;


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
}