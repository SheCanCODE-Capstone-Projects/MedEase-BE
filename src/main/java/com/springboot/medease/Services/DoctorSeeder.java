package com.springboot.medease.Services;

import com.springboot.medease.Models.DoctorProfile;
import com.springboot.medease.Models.User;
import com.springboot.medease.Models.UserType;
import com.springboot.medease.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Component
public class DoctorSeeder implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(DoctorSeeder.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        String doctorEmail = "dr.janembabazi123@gmail.com";

        if (!userRepository.existsByEmail(doctorEmail)) {

            User doctor = new User();
            doctor.setFirstName("Jane");
            doctor.setLastName("Mbabazi");
            doctor.setEmail(doctorEmail);
            doctor.setPhoneNumber("0788000002");
            doctor.setPassword(passwordEncoder.encode("123456"));
            doctor.setUserType(UserType.ROLE_DOCTOR);
            doctor.setCreatedAt(LocalDateTime.now());
            doctor.setUpdatedAt(LocalDateTime.now());

            DoctorProfile doctorProfile = new DoctorProfile();
            doctorProfile.setDoctorSpecialization("Cardiology");
            doctorProfile.setDoctorLicenseNumber("DOC-987654");

            doctor.setDoctorProfile(doctorProfile);

            userRepository.save(doctor);

            logger.info("Doctor " + doctor.getFirstName() + " " + doctor.getLastName() + " seeded successfully.");

        } else {
            logger.info("Doctor with email " + doctorEmail + " already exists. Skipping seeding.");
        }

        }

    }

