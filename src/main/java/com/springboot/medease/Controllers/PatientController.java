package com.springboot.medease.Controllers;

import com.springboot.medease.DTOs.MedicalInfoUpdateRequest;
import com.springboot.medease.DTOs.PatientUpdateRequest;
import com.springboot.medease.Models.Patient;
import com.springboot.medease.Services.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;


    // Update personal info (Patient)
    @PutMapping("/{id}/update-personal")
    public Patient updatePersonalInfo(
            @PathVariable String id,
            @RequestBody  @Valid PatientUpdateRequest dto,
            HttpServletRequest request
    ) {

        return patientService.updatePatientInfo(id, dto);
    }


    // Update medical info (Doctor)
    @PutMapping("/{id}/update-medical")
    public Patient updateMedicalInfo(
            @PathVariable String id,
            @RequestBody @Valid  MedicalInfoUpdateRequest dto,
            @RequestHeader("doctor-id") @NotBlank String doctorId
    ) {
        return patientService.updateMedicalInfo(id, dto, doctorId);
    }


    // Fetch patient by ID
    @GetMapping("/{id}")
    public Patient getPatient(@PathVariable String id) {
        return patientService.getById(id);
    }
}
