package com.springboot.medease.Services;

import com.springboot.medease.DTOs.ConsultationRequestDTO;
import com.springboot.medease.DTOs.ConsultationResponseDTO;
import com.springboot.medease.Models.Consultation;
import com.springboot.medease.Repository.ConsultationRepository;
import com.springboot.medease.mapper.ConsultationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ConsultationService {

    private final ConsultationRepository repository;

    public ConsultationResponseDTO save(ConsultationRequestDTO request) {
        Consultation consultation = ConsultationMapper.toDocument(request);
        Consultation saved = repository.save(consultation);
        return ConsultationMapper.toDTO(saved);
    }

    public List<ConsultationResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(ConsultationMapper::toDTO)
                .toList();
    }

    public Optional<ConsultationResponseDTO> findById(String id) {
        return repository.findById(id)
                .map(ConsultationMapper::toDTO);
    }

    public List<ConsultationResponseDTO> findByPatientId(String patientId) {
        return repository.findByPatientId(patientId).stream()
                .map(ConsultationMapper::toDTO)
                .toList();
    }

    public List<ConsultationResponseDTO> findByClinicId(String clinicId) {
        return repository.findByClinicId(clinicId).stream()
                .map(ConsultationMapper::toDTO)
                .toList();
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public long count() {
        return repository.count();
    }
}
