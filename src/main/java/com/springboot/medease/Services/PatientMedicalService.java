package com.springboot.medease.Services;


import com.springboot.medease.DTOs.MedicalRecordRequest;
import com.springboot.medease.Models.MedicalRecord;
import com.springboot.medease.Models.Patient;
import com.springboot.medease.Repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PatientMedicalService {

    private final PatientRepository patientRepository;

    public Patient addChronicDisease(
            String patientId,
            MedicalRecordRequest request,
            String doctorId
    ) {
        Patient patient = getPatient(patientId);

        patient.getChronicDiseases().add(
                MedicalRecord.builder()
                        .name(request.getName())
                        .diagnosingDoctorId(doctorId)
                        .recordedAt(LocalDate.now())
                        .build()
        );

        return patientRepository.save(patient);
    }

    public Patient addAllergy(
            String patientId,
            MedicalRecordRequest request,
            String doctorId
    ) {
        Patient patient = getPatient(patientId);

        patient.getAllergies().add(
                MedicalRecord.builder()
                        .name(request.getName())
                        .diagnosingDoctorId(doctorId)
                        .recordedAt(LocalDate.now())
                        .build()
        );

        return patientRepository.save(patient);
    }

    public Patient viewMedicalHistory(String patientId) {
        return getPatient(patientId);
    }

    private Patient getPatient(String id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }
}
