package com.springboot.medease.Repository;

import com.springboot.medease.Models.Clinic;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClinicRepository extends MongoRepository<Clinic, String> {
}