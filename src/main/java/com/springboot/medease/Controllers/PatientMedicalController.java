package com.springboot.medease.Controllers;

import com.springboot.medease.DTOs.MedicalRecordRequest;
import com.springboot.medease.Models.Patient;
import com.springboot.medease.Services.PatientMedicalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patients/{patientId}/medical")
public class PatientMedicalController {

    private final PatientMedicalService service;

    // VIEW (No auth required for testing)
    @GetMapping
    public Patient view(@PathVariable String patientId) {
        return service.viewMedicalHistory(patientId);
    }

    // ADD CHRONIC DISEASE (No auth required for testing)
    @PostMapping("/chronic-diseases")
    public Patient addChronicDisease(
            @PathVariable String patientId,
            @RequestBody @Valid MedicalRecordRequest request
    ) {
        return service.addChronicDisease(
                patientId,
                request,
                "doctor123" // hardcoded for testing
        );
    }

    // ADD ALLERGY (No auth required for testing)
    @PostMapping("/allergies")
    public Patient addAllergy(
            @PathVariable String patientId,
            @RequestBody @Valid MedicalRecordRequest request
    ) {
        return service.addAllergy(
                patientId,
                request,
                "doctor123" // hardcoded for testing
        );
    }
}

