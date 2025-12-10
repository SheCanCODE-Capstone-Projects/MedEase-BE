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

        if (dto.getFirstName() != null) patient.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) patient.setLastName(dto.getLastName());
        if (dto.getEmail() != null) patient.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) patient.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getDateOfBirth() != null) patient.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getGender() != null) patient.setGender(dto.getGender());

        if (dto.getInsuranceProvider() != null)
            patient.setInsuranceProvider(dto.getInsuranceProvider());
        if (dto.getInsuranceNumber() != null)
            patient.setInsuranceNumber(dto.getInsuranceNumber());
        return patientRepo.save(patient);
    }


    // Update medical info (Doctor only)
    public Patient updateMedicalInfo(String patientId, MedicalInfoUpdateRequest dto, String doctorId) {
        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        MedicalInfo medicalInfo = patient.getMedicalInfo();
        if (medicalInfo == null) medicalInfo = new MedicalInfo();

        boolean updated = false;
        if (dto.getChronicDiseases() != null) {
            medicalInfo.setChronicDiseases(dto.getChronicDiseases());
            updated = true;
        }
        if (dto.getMedicationAllergies() != null) {
            medicalInfo.setMedicationAllergies(dto.getMedicationAllergies());
            updated = true;
        }
        
        if (updated) {
            medicalInfo.setUpdatedByDoctorId(doctorId);
        }

        patient.setMedicalInfo(medicalInfo);
        return patientRepo.save(patient);
    }

    // Add new patient
    public Patient addPatient(Patient patient) {
        return patientRepo.save(patient);
    }

    // Fetch patient by ID
    public Patient getById(String patientId) {
        return patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }


}