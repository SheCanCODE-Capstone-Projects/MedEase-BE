package com.springboot.medease.Services;

import com.springboot.medease.DTOs.MedicalInfoUpdateRequest;
import com.springboot.medease.DTOs.PatientUpdateRequest;
import com.springboot.medease.Models.MedicalInfo;
import com.springboot.medease.Models.Patient;
import com.springboot.medease.Repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepo;


    // Update patient personal info (Patient only)
    public Patient updatePatientInfo(String patientId, PatientUpdateRequest dto) {
        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setEmail(dto.getEmail());
        patient.setPhoneNumber(dto.getPhoneNumber());
//        patient.setDateOfBirth(dto.getDateOfBirth());
//        patient.setGender(dto.getGender());

        if (dto.getInsuranceProvider() != null)
            patient.setUserType(dto.getInsuranceProvider().isEmpty() ? patient.getUserType() : patient.getUserType());


        return patientRepo.save(patient);
    }


    // Update medical info (Doctor only)
    public Patient updateMedicalInfo(String patientId, MedicalInfoUpdateRequest dto, String doctorId) {
        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        MedicalInfo medicalInfo = patient.getMedicalInfo();
        if (medicalInfo == null) medicalInfo = new MedicalInfo();

        medicalInfo.setChronicDiseases(dto.getChronicDiseases());
        medicalInfo.setMedicationAllergies(dto.getMedicationAllergies());
        medicalInfo.setUpdatedByDoctorId(doctorId);

        patient.setMedicalInfo(medicalInfo);
        return patientRepo.save(patient);
    }

    // Fetch patient by ID
    public Patient getById(String patientId) {
        return patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }}