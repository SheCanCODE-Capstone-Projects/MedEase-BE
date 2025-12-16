package com.springboot.medease.mapper;

import com.springboot.medease.DTOs.ConsultationRequestDTO;
import com.springboot.medease.DTOs.ConsultationResponseDTO;
import com.springboot.medease.Models.Consultation;

public class ConsultationMapper {

    public static Consultation toDocument(ConsultationRequestDTO dto) {
        Consultation c = new Consultation();
        c.setDiagnosis(dto.getDiagnosis());
        c.setSymptoms(dto.getSymptoms());
        c.setDoctorId(dto.getDoctorId());
        c.setPatientId(dto.getPatientId());
        c.setClinicId(dto.getClinicId());
        return c;
    }

    public static ConsultationResponseDTO toDTO(Consultation doc) {
        ConsultationResponseDTO dto = new ConsultationResponseDTO();
        dto.setId(doc.getId());
        dto.setDiagnosis(doc.getDiagnosis());
        dto.setSymptoms(doc.getSymptoms());
        dto.setDoctorId(doc.getDoctorId());
        dto.setPatientId(doc.getPatientId());
        dto.setClinicId(doc.getClinicId());
        dto.setTimestamp(doc.getTimestamp());
        return dto;
    }
}
