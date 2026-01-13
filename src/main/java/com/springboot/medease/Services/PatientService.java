package com.springboot.medease.Services;

import com.springboot.medease.DTOs.MedicalInfoUpdateRequest;
import com.springboot.medease.DTOs.PatientResponseDTO;
import com.springboot.medease.DTOs.PatientUpdateRequest;
import com.springboot.medease.Models.MedicalInfo;
import com.springboot.medease.Models.Patient;
import com.springboot.medease.Models.User;
import com.springboot.medease.Models.PatientProfile;
import java.util.ArrayList;
import com.springboot.medease.Repository.PatientRepository;
import com.springboot.medease.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepo;
    private final UserRepository userRepository;


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
    public PatientResponseDTO addPatient(PatientUpdateRequest dto) {
        Patient patient = new Patient();
        patient.setPatientReference(generatePatientReference());
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setEmail(dto.getEmail());
        patient.setPhoneNumber(dto.getPhoneNumber());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setGender(dto.getGender());
        patient.setInsuranceProvider(dto.getInsuranceProvider());
        patient.setInsuranceNumber(dto.getInsuranceNumber());

        // Create or update container
        String containerId = "CONTAINER_USER";
        User container = userRepository.findById(containerId).orElse(new User());
        container.setId(containerId);
        userRepository.save(container);

        return mapToDTO(patientRepo.save(patient));
    }

    // Add patient by doctor with container association
    public PatientResponseDTO addPatientByDoctor(PatientUpdateRequest dto, String doctorId) {
        Patient patient = new Patient();
        patient.setPatientReference(generatePatientReference());
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setEmail(dto.getEmail());
        patient.setPhoneNumber(dto.getPhoneNumber());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setGender(dto.getGender());
        patient.setInsuranceProvider(dto.getInsuranceProvider());
        patient.setInsuranceNumber(dto.getInsuranceNumber());

        Patient savedPatient = patientRepo.save(patient);

        // Create PatientProfile and add to User container
        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setFirstName(dto.getFirstName());
        patientProfile.setLastName(dto.getLastName());
        patientProfile.setEmail(dto.getEmail());
        patientProfile.setPhoneNumber(dto.getPhoneNumber());
        patientProfile.setDateOfBirth(dto.getDateOfBirth());
        patientProfile.setGender(dto.getGender());
        patientProfile.setInsuranceProvider(dto.getInsuranceProvider());
        patientProfile.setInsuranceNumber(dto.getInsuranceNumber());

        String containerId = "MAIN_USER_CONTAINER";
        User container = userRepository.findById(containerId).orElse(new User());
        container.setId(containerId);
        if (container.getPatients() == null) {
            container.setPatients(new ArrayList<>());
        }
        container.getPatients().add(patientProfile);
        userRepository.save(container);

        return mapToDTO(savedPatient);
    }

    private PatientResponseDTO mapToDTO(Patient patient) {
        PatientResponseDTO dto = new PatientResponseDTO();
        dto.setId(patient.getId());
        dto.setPatientReference(patient.getPatientReference());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setEmail(patient.getEmail());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setGender(patient.getGender());
        dto.setInsuranceProvider(patient.getInsuranceProvider());
        dto.setInsuranceNumber(patient.getInsuranceNumber());
        dto.setMedicalInfo(patient.getMedicalInfo());
        return dto;
    }

    // Fetch patient by ID
    public Patient getById(String patientId) {
        return patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    private String generatePatientReference() {
        return "PAT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public Patient getByReference(String reference) {
        return patientRepo.findByPatientReference(reference)
                .orElseThrow(() -> new RuntimeException("Patient not found with reference: " + reference));
    }

}
