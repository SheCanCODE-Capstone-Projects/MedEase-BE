package com.springboot.medease.mapper;

import com.springboot.medease.DTOs.ConsultationRequestDTO;
import com.springboot.medease.DTOs.ConsultationResponseDTO;
import com.springboot.medease.Models.Consultation;

import java.time.Instant;

public class ConsultationMapper {

    public static Consultation toDocument(ConsultationRequestDTO dto) {
        Consultation consultation = new Consultation();
        consultation.setDiagnosis(dto.getDiagnosis());
        consultation.setSymptoms(dto.getSymptoms());
        consultation.setDoctorId(dto.getDoctorId());
        consultation.setPatientId(dto.getPatientId());
        consultation.setClinicId(dto.getClinicId());
        consultation.setTimestamp(Instant.now()); // consultation time
        return consultation;
    }

    public static ConsultationResponseDTO toDTO(Consultation consultation) {
        ConsultationResponseDTO dto = new ConsultationResponseDTO();
        dto.setId(consultation.getId());
        dto.setDiagnosis(consultation.getDiagnosis());
        dto.setSymptoms(consultation.getSymptoms());
        dto.setDoctorId(consultation.getDoctorId());
        dto.setPatientId(consultation.getPatientId());
        dto.setClinicId(consultation.getClinicId());
        dto.setTimestamp(consultation.getTimestamp());
        return dto;
    }
}
