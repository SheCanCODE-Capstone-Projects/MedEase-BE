package com.springboot.medease.Repository;

import com.springboot.medease.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

    @Query("{ 'doctors': { $elemMatch: { 'name': { $regex: ?0, $options: 'i' } } } }")
    List<User> findUsersWithDoctorByName(String doctorName);
}
