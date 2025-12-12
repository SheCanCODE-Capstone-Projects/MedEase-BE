package com.springboot.medease.Repository;

import com.springboot.medease.Models.Service;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ServiceRepository extends MongoRepository<Service, String> {
    List<Service> findByClinicId(String clinicId);
}