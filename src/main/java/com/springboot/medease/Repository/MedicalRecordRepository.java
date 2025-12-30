package com.springboot.medease.Repository;

import com.springboot.medease.Models.MedicalRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MedicalRecordRepository extends MongoRepository<MedicalRecord,String> {


    Optional<MedicalRecord> findByPatientId(String patientId);
}
