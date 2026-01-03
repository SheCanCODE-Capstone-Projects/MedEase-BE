package com.springboot.medease.Controllers;

import com.springboot.medease.DTOs.ConsultationRequestDTO;
import com.springboot.medease.DTOs.ConsultationResponseDTO;
import com.springboot.medease.Services.ConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final ConsultationService service;

    // CREATE CONSULTATION
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultationResponseDTO create(
            @Valid @RequestBody ConsultationRequestDTO request,
            @AuthenticationPrincipal UserDetails user
    ) {

        return service.save(request);
    }

    //GET ALL
    @GetMapping
    public List<ConsultationResponseDTO> getAll() {
        return service.findAll();
    }

    //GET BY ID
    @GetMapping("/{id}")
    public ConsultationResponseDTO getById(@PathVariable String id) {
        return service.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation not found"));
    }

    // GET BY PATIENT
    @GetMapping("/patient/{patientId}")
    public List<ConsultationResponseDTO> getByPatientId(@PathVariable String patientId) {
        return service.findByPatientId(patientId);
    }

    //GET BY CLINIC
    @GetMapping("/clinic/{clinicId}")
    public List<ConsultationResponseDTO> getByClinicId(@PathVariable String clinicId) {
        return service.findByClinicId(clinicId);
    }

    // COUNT
    @GetMapping("/count")
    public long getCount() {
        return service.count();
    }

    //DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.deleteById(id);
    }
}
