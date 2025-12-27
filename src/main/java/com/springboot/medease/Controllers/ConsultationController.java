package com.springboot.medease.Controllers;

import com.springboot.medease.DTOs.ConsultationRequestDTO;

import com.springboot.medease.DTOs.ConsultationResponseDTO;

import com.springboot.medease.Services.ConsultationService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/consultation")
public class ConsultationController {


    private final ConsultationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultationResponseDTO create(@Valid @RequestBody ConsultationRequestDTO request) {
        return service.save(request);
    }

    @GetMapping
    public List<ConsultationResponseDTO> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ConsultationResponseDTO getById(@PathVariable String id) {
        return service.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation not found"));
    }

    @GetMapping("/patient/{patientId}")
    public List<ConsultationResponseDTO> getByPatientId(@PathVariable String patientId) {
        return service.findByPatientId(patientId);
    }

    @GetMapping("/clinic/{clinicId}")
    public List<ConsultationResponseDTO> getByClinicId(@PathVariable String clinicId) {
        return service.findByClinicId(clinicId);
    }

    @GetMapping("/count")
    public long getCount() {
        return service.count();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.deleteById(id);
    }
}
