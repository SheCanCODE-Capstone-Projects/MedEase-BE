package com.springboot.medease.Services;

import com.springboot.medease.Models.User;
import com.springboot.medease.Models.UserType;
import com.springboot.medease.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DoctorSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (!userRepository.existsByEmail("dr.janembabazi123@gmail.com.com")) {

            User doctor = new User();
            doctor.setFirstName("Jane");
            doctor.setLastName("Mbabazi");
            doctor.setEmail("dr.janembabazi123@gmail.com.com");
            doctor.setPhoneNumber("0788000002");
            doctor.setPassword(passwordEncoder.encode("123456"));
            doctor.setUserType(UserType.DOCTOR);
            doctor.setCreatedAt(LocalDateTime.now());
            doctor.setUpdatedAt(LocalDateTime.now());

            doctor.setDoctorLicenseNumber("DOC-987654");
            doctor.setDoctorSpecialization("Cardiology");

            userRepository.save(doctor);

        }

    }
}

