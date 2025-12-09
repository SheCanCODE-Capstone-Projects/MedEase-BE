package com.springboot.medease.Repository;

import com.springboot.medease.Models.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PatientRepository extends MongoRepository<Patient, String> {
}
