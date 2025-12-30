package com.springboot.medease.mapper;

import com.springboot.medease.DTOs.ChronicConditionRequestDTO;
import com.springboot.medease.DTOs.ChronicConditionResponseDTO;
import com.springboot.medease.Models.ChronicCondition;

import java.time.LocalDate;

public class ChronicConditionMapper {
    public static ChronicCondition toEntity(
            ChronicConditionRequestDTO dto,
            String doctorId
    ) {
        ChronicCondition c = new ChronicCondition();
        c.setName(dto.getName());
        c.setType(dto.getType());
        c.setDiagnosedByDoctorId(doctorId);
        c.setDiagnosedDate(LocalDate.now());
        return c;
    }

    public static ChronicConditionResponseDTO toDTO(ChronicCondition entity) {
        ChronicConditionResponseDTO dto = new ChronicConditionResponseDTO();
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setDiagnosedByDoctorId(entity.getDiagnosedByDoctorId());
        dto.setDiagnosedDate(entity.getDiagnosedDate());
        return dto;
    }
}
