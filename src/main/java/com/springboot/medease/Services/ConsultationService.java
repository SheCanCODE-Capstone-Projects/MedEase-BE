package com.springboot.medease.Services;

import com.springboot.medease.DTOs.ConsultationRequestDTO;
import com.springboot.medease.DTOs.ConsultationResponseDTO;
import com.springboot.medease.Models.Consultation;
import com.springboot.medease.Models.ConsultationRef;
import com.springboot.medease.Repository.ClinicRepository;
import com.springboot.medease.Repository.ConsultationRepository;
import com.springboot.medease.Repository.PatientRepository;
import com.springboot.medease.mapper.ConsultationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ConsultationService {

    private final ConsultationRepository repository;
    private final PatientRepository patientRepo;
    private final ClinicRepository clinicRepo;

    // SAVE CONSULTATION 
    @PreAuthorize("hasRole('DOCTOR')")
    public ConsultationResponseDTO save(ConsultationRequestDTO request) {
        validateConsultationRequest(request);


        Consultation consultation = ConsultationMapper.toDocument(request);

        // Set timestamp if not set
        if (consultation.getTimestamp() == null) {
            consultation.setTimestamp(Instant.now());
        }


        Consultation saved = repository.save(consultation);


        ConsultationRef patientRef = new ConsultationRef();
        patientRef.setConsultationId(saved.getId());
        patientRef.setTimestamp(LocalDateTime.now());
        patientRepo.addConsultation(saved.getPatientId(), patientRef);

        // Add reference to clinic
        ConsultationRef clinicRef = new ConsultationRef();
        clinicRef.setConsultationId(saved.getId());
        clinicRef.setTimestamp(LocalDateTime.now());
        clinicRepo.addConsultation(saved.getClinicId(), clinicRef);

        // Return DTO
        return ConsultationMapper.toDTO(saved);
    }

    // VALIDATION
    private void validateConsultationRequest(ConsultationRequestDTO request) {
        if (!StringUtils.hasText(request.getDiagnosis()))
            throw new IllegalArgumentException("Diagnosis cannot be empty");
        if (!StringUtils.hasText(request.getSymptoms()))
            throw new IllegalArgumentException("Symptoms cannot be empty");
        if (!StringUtils.hasText(request.getDoctorId()))
            throw new IllegalArgumentException("Doctor ID cannot be empty");
        if (!StringUtils.hasText(request.getPatientId()))
            throw new IllegalArgumentException("Patient ID cannot be empty");
        if (!StringUtils.hasText(request.getClinicId()))
            throw new IllegalArgumentException("Clinic ID cannot be empty");
    }

    //  FIND METHODS
    public List<ConsultationResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(ConsultationMapper::toDTO)
                .toList();
    }

    public Optional<ConsultationResponseDTO> findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("Consultation ID cannot be empty");
        }
        return repository.findById(id)
                .map(ConsultationMapper::toDTO);
    }

    public List<ConsultationResponseDTO> findByPatientId(String patientId) {
        if (!StringUtils.hasText(patientId)) {
            throw new IllegalArgumentException("Patient ID cannot be empty");
        }
        return repository.findByPatientId(patientId).stream()
                .map(ConsultationMapper::toDTO)
                .toList();
    }

    public List<ConsultationResponseDTO> findByClinicId(String clinicId) {
        if (!StringUtils.hasText(clinicId)) {
            throw new IllegalArgumentException("Clinic ID cannot be empty");
        }
        return repository.findByClinicId(clinicId).stream()
                .map(ConsultationMapper::toDTO)
                .toList();
    }

    //  DELETE / COUNT
    public void deleteById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("Consultation ID cannot be empty");
        }
        if (!repository.existsById(id)) {
            throw new RuntimeException("Consultation not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    public long count() {
        return repository.count();
    }
}