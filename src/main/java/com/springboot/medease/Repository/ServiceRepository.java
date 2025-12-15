package com.springboot.medease.Repository;

import com.springboot.medease.Models.Service;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends MongoRepository<Service, String> {
    List<Service> findByClinicId(String clinicId);
    Optional<Service> findByClinicIdAndNameIgnoreCase(String clinicId, String name);
}