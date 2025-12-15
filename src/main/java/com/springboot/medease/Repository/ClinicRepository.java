package com.springboot.medease.Repository;

import com.springboot.medease.Models.Clinic;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClinicRepository extends MongoRepository<Clinic, String> {
    List<Clinic> findByNameIgnoreCase(String name);


}