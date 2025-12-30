package com.springboot.medease.Services;

import com.springboot.medease.DTOs.ChronicConditionRequestDTO;
import com.springboot.medease.DTOs.ChronicConditionResponseDTO;
import com.springboot.medease.Models.AuditLog;
import com.springboot.medease.Models.ChronicCondition;
import com.springboot.medease.Models.MedicalRecord;
import com.springboot.medease.Repository.AuditLogRepository;
import com.springboot.medease.Repository.MedicalRecordRepository;
import com.springboot.medease.mapper.ChronicConditionMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChronicConditionService {
    
        private final MedicalRecordRepository medicalRecordRepository;
        private final AuditLogRepository auditLogRepository;

        public ChronicConditionService(
                MedicalRecordRepository medicalRecordRepository,
                AuditLogRepository auditLogRepository
        ) {
            this.medicalRecordRepository = medicalRecordRepository;
            this.auditLogRepository = auditLogRepository;
        }

        private MedicalRecord getRecord(String patientId) {
            return medicalRecordRepository.findByPatientId(patientId)
                    .orElseThrow(() -> new RuntimeException("Medical record not found"));
        }

        

        @PreAuthorize("hasAnyRole('DOCTOR','PHARMACIST')")
        public List<ChronicConditionResponseDTO> viewConditions(String patientId) {
            return getRecord(patientId).getChronicConditions().stream()
                    .filter(ChronicCondition::isActive)
                    .map(ChronicConditionMapper::toDTO)
                    .toList();
        }

       

        @PreAuthorize("hasRole('DOCTOR')")
        public void addCondition(
                String patientId,
                ChronicConditionRequestDTO dto,
                String doctorId
        ) {
            MedicalRecord record = getRecord(patientId);

            ChronicCondition condition =
                    ChronicConditionMapper.toEntity(dto, doctorId);

            condition.setVersion(1);
            condition.setActive(true);

            record.getChronicConditions().add(condition);
            medicalRecordRepository.save(record);

            auditLogRepository.save(
                    new AuditLog(
                            "ADD_CHRONIC_CONDITION",
                            doctorId,
                            patientId,
                            LocalDateTime.now()
                    )
            );
        }

        // UPDATE (VERSIONED)

        @PreAuthorize("hasRole('DOCTOR')")
        public void updateCondition(
                String patientId,
                String conditionId,
                ChronicConditionRequestDTO dto,
                String doctorId
        ) {
            MedicalRecord record = getRecord(patientId);

            ChronicCondition old = record.getChronicConditions().stream()
                    .filter(c -> c.getId().equals(conditionId) && c.isActive())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Active condition not found"));

            old.setActive(false);

            ChronicCondition updated =
                    ChronicConditionMapper.toEntity(dto, doctorId);

            updated.setVersion(old.getVersion() + 1);
            updated.setActive(true);

            record.getChronicConditions().add(updated);
            medicalRecordRepository.save(record);

            auditLogRepository.save(
                    new AuditLog(
                            "UPDATE_CHRONIC_CONDITION",
                            doctorId,
                            patientId,
                            LocalDateTime.now()
                    )
            );
        }
}