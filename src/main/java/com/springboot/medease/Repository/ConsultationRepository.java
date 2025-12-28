package com.springboot.medease.Repository;

import com.springboot.medease.Models.Consultation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConsultationRepository extends MongoRepository<Consultation,String> {

    List<Consultation> findByPatientId(String patientId);

    List<Consultation> findByClinicId(String clinicId);
}
