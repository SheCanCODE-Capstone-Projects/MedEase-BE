package com.springboot.medease.Repository;

import com.springboot.medease.Models.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PatientRepository extends MongoRepository<Patient, String> {
    boolean existsByPatientReference(String patientReference);
    Optional<Patient> findByPatientReference(String reference);
}
