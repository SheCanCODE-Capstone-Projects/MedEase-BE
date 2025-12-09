package com.springboot.medease.Controllers;


import com.springboot.medease.DTOs.MedicalInfoUpdateRequest;
import com.springboot.medease.DTOs.PersonalInfoUpdateRequest;
import com.springboot.medease.Models.Patient;
import com.springboot.medease.Services.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService service;

    // Patient updates PERSONAL info
    @PutMapping("/{id}/personal-info")
    public Patient updatePersonal(
            @PathVariable String id,
            @RequestBody PersonalInfoUpdateRequest dto
    ) {
        return service.updatePersonalInfo(id, dto);
    }

    // Doctor updates MEDICAL info
    @PutMapping("/{id}/medical-info")
    public Patient updateMedical(
            @PathVariable String id,
            @RequestBody MedicalInfoUpdateRequest dto,
            @RequestHeader("doctor-id") String doctorId
    ) {
        return service.updateMedicalInfo(id, dto, doctorId);
    }
}
