package com.springboot.medease.Controllers;

import com.springboot.medease.DTOs.MedicalInfoUpdateRequest;
import com.springboot.medease.DTOs.PatientResponseDTO;
import com.springboot.medease.DTOs.PatientUpdateRequest;
import com.springboot.medease.Models.Patient;
import com.springboot.medease.Services.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;


    // Update personal info (Patient)
    @PutMapping("/{id}/update-personal")
    public PatientResponseDTO updatePersonalInfo(
            @PathVariable String id,
            @RequestBody  @Valid PatientUpdateRequest dto,
            HttpServletRequest request
    ) {

        Patient patient = patientService.updatePatientInfo(id, dto);
        return mapToResponseDTO(patient);
    }


    // Update medical info (Doctor)
    @PutMapping("/{id}/update-medical")
    @PreAuthorize("hasRole('DOCTOR')")
    public PatientResponseDTO updateMedicalInfo(
            @PathVariable String id,
            @RequestBody @Valid MedicalInfoUpdateRequest dto,
            @RequestHeader("doctor-id") @NotBlank String doctorId,
            Authentication authentication
    ) {
        if (!doctorId.equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Doctor ID mismatch");
        }
        Patient patient = patientService.updateMedicalInfo(id, dto, doctorId);
        return mapToResponseDTO(patient);
    }


    // Fetch patient by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or authentication.name == #id")
    public PatientResponseDTO getPatient(@PathVariable String id, Authentication authentication) {
        Patient patient = patientService.getById(id);
        return mapToResponseDTO(patient);
    }

    private PatientResponseDTO mapToResponseDTO(Patient patient) {
        PatientResponseDTO dto = new PatientResponseDTO();
        dto.setId(patient.getId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setEmail(patient.getEmail());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setGender(patient.getGender());
        dto.setMedicalInfo(patient.getMedicalInfo());
        dto.setSubjective(patient.getSubjective());
        return dto;
    }
}
