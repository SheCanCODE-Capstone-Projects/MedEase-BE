package com.springboot.medease.Repository;
import com.springboot.medease.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("{'pharmacistProfile.pharmacistLicenseNumber': ?0}")
    Boolean existsByPharmacistProfilePharmacistLicenseNumber(String pharmacistLicenseNumber);

    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("{'pharmacistProfile.pharmacistLicenseNumber': ?0}")
    Optional<User> findByPharmacistProfilePharmacistLicenseNumber(String licenseNumber);
}

