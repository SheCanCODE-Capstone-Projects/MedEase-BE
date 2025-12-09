package com.springboot.medease.Services;

import com.springboot.medease.DTOs.MedicalInfoUpdateRequest;
import com.springboot.medease.DTOs.PersonalInfoUpdateRequest;
import com.springboot.medease.Models.MedicalInfo;
import com.springboot.medease.Models.Patient;
import com.springboot.medease.Models.PersonalInfo;
import com.springboot.medease.Repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repo;

    public Patient updatePersonalInfo(String patientId, PersonalInfoUpdateRequest dto){
        Patient patient = repo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        PersonalInfo info = patient.getPersonalInfo();

        if (info == null) info = new PersonalInfo();

        info.setName(dto.getName());
        info.setPhone(dto.getPhone());
        info.setEmail(dto.getEmail());
        info.setDateOfBirth(dto.getDateOfBirth());
        info.setGender(dto.getGender());
        info.setInsuranceProvider(dto.getInsuranceProvider());

        patient.setPersonalInfo(info);
        return repo.save(patient);
    }
    public Patient updateMedicalInfo(String patientId, MedicalInfoUpdateRequest dto, String doctorId) {
        Patient patient = repo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        MedicalInfo medicalInfo = patient.getMedicalInfo();
        if (medicalInfo == null) medicalInfo = new MedicalInfo();

        medicalInfo.setChronicDiseases(dto.getChronicDiseases());
        medicalInfo.setMedicationAllergies(dto.getMedicationAllergies());
        medicalInfo.setUpdatedByDoctorId(doctorId);

        patient.setMedicalInfo(medicalInfo);

        return repo.save(patient);
    }


}
