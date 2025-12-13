package com.springboot.medease.Controllers;

import com.springboot.medease.DTOs.JoinQueueRequest;
import com.springboot.medease.DTOs.QueueResponseDTO;
import com.springboot.medease.Security.CustomUserPrincipal;
import com.springboot.medease.Services.QueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/join")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<QueueResponseDTO> joinQueue(
            @RequestBody @Valid JoinQueueRequest request,
            Authentication authentication) {

        CustomUserPrincipal user = (CustomUserPrincipal) authentication.getPrincipal();
        String patientId = user.getUserId(); // immutable ID from JWT

        QueueResponseDTO dto = queueService.joinQueue(patientId, request);

        URI location = URI.create("/api/queue/" + dto.getQueueId()); // optional Location header
        return ResponseEntity.created(location).body(dto);
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
}