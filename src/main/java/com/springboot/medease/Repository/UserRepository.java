package com.springboot.medease.Repository;
import com.springboot.medease.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByPatientsEmail(String email);
    boolean existsByPharmacistsEmail(String email);
    boolean existsByPharmacistsPhoneNumber(String phone);
    boolean existsByPatientsPhoneNumber(String phone);
    boolean existsByPharmacistsPharmacistLicenseNumber(String license);
    boolean existsByDoctorsEmail(String email);
    boolean existsByDoctorsPhoneNumber(String phone);

    Optional<User> findByPatientsEmail(String email);
    Optional<User> findByDoctorsEmail(String email);
    Optional<User> findByPharmacistsEmail(String email);

    Optional<User> findByPatientsPhoneNumber(String phone);
    Optional<User> findByDoctorsPhoneNumber(String phone);
    Optional<User> findByPharmacistsPhoneNumber(String phone);

    @Query("{ 'doctors': { $elemMatch: { 'name': { $regex: ?0, $options: 'i' } } } }")
    List<User> findUsersWithDoctorByName(String doctorName);
}
