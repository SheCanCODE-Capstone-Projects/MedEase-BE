package com.springboot.medease.Repository;
import com.springboot.medease.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByPatientsEmail(String email);
    boolean existsByPharmacistsPharmacyEmail(String email);
    boolean existsByPharmacistsPhoneNumber(String phone);
    boolean existsByPatientsPhoneNumber(String phone);
    boolean existsByPharmacistsPharmacistLicenseNumber(String license);

}

