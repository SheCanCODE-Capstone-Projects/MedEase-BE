package com.springboot.medease.Repository;

import com.springboot.medease.Models.ConsultationRef;
import com.springboot.medease.Models.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.Optional;

public interface PatientRepository extends MongoRepository<Patient, String> {
    boolean existsByPatientReference(String patientReference);
    Optional<Patient> findByPatientReference(String reference);

    @Query("{ '_id': ?0 }")
    @Update("{ '$push': { 'consultations': ?1 } }")
    void addConsultation(String patientId, ConsultationRef ref);

}
