package com.springboot.medease.Services;

import com.springboot.medease.DTOs.ConsultationRequestDTO;
import com.springboot.medease.DTOs.ConsultationResponseDTO;
import com.springboot.medease.Models.Consultation;
import com.springboot.medease.Repository.ConsultationRepository;
import com.springboot.medease.mapper.ConsultationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ConsultationService {

    private final ConsultationRepository repository;

    public ConsultationResponseDTO save(ConsultationRequestDTO request) {
        validateConsultationRequest(request);
        Consultation consultation = ConsultationMapper.toDocument(request);
        Consultation saved = repository.save(consultation);
        return ConsultationMapper.toDTO(saved);
    }

    private void validateConsultationRequest(ConsultationRequestDTO request) {
        if (!StringUtils.hasText(request.getDiagnosis())) {
            throw new IllegalArgumentException("Diagnosis cannot be empty");
        }
        if (!StringUtils.hasText(request.getSymptoms())) {
            throw new IllegalArgumentException("Symptoms cannot be empty");
        }
        if (!StringUtils.hasText(request.getDoctorId())) {
            throw new IllegalArgumentException("Doctor ID cannot be empty");
        }
        if (!StringUtils.hasText(request.getPatientId())) {
            throw new IllegalArgumentException("Patient ID cannot be empty");
        }
        if (!StringUtils.hasText(request.getClinicId())) {
            throw new IllegalArgumentException("Clinic ID cannot be empty");
        }
    }

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
