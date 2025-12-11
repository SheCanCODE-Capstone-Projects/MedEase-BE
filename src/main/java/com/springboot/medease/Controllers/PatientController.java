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
    @PreAuthorize("hasRole('PATIENT')")
    public PatientResponseDTO updatePersonalInfo(
            @PathVariable String id,
            @RequestBody  @Valid PatientUpdateRequest dto,
            Authentication authentication
    ) {
        Patient patient = patientService.getById(id);
        if (!patient.getEmail().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        patient = patientService.updatePatientInfo(id, dto);
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


    // Add new patient (Admin/Doctor)
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public PatientResponseDTO addPatient(@RequestBody @Valid PatientUpdateRequest dto) {
        return patientService.addPatient(dto);
    }

    // Fetch patient by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('PATIENT')")
    public PatientResponseDTO getPatient(@PathVariable String id, Authentication authentication) {
        Patient patient = patientService.getById(id);
        if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_DOCTOR") || a.getAuthority().equals("ROLE_ADMIN"))) {
            if (!patient.getEmail().equals(authentication.getName())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }
        }
        return mapToResponseDTO(patient);
    }

    private PatientResponseDTO mapToResponseDTO(Patient patient) {
        PatientResponseDTO dto = new PatientResponseDTO();
        dto.setId(patient.getId());
        dto.setPatientReference(patient.getPatientReference());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setEmail(patient.getEmail());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setGender(patient.getGender());
        dto.setMedicalInfo(patient.getMedicalInfo());
        return dto;
    }

    @GetMapping("/by-reference/{ref}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PHARMACIST')")
    public PatientResponseDTO getPatientByReference(@PathVariable String ref) {
        Patient patient = patientService.getByReference(ref);
        return mapToResponseDTO(patient);
    }
}
