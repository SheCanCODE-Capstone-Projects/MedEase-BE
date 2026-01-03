package com.springboot.medease.Repository;

import com.springboot.medease.Models.Clinic;
import com.springboot.medease.Models.ConsultationRef;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;

public interface ClinicRepository extends MongoRepository<Clinic, String> {
    List<Clinic> findByNameIgnoreCase(String name);

    @Query("{ '_id': ?0 }")
    @Update("{ '$push': { 'consultations': ?1 } }")
    void addConsultation(String clinicId, ConsultationRef ref);


}