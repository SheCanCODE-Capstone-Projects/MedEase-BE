package com.springboot.medease.Services;

import com.mongodb.lang.NonNull;
import com.springboot.medease.Models.*;
import com.springboot.medease.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class DoctorSeeder implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(DoctorSeeder.class.getName());

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public void run(@NonNull String... args) {

        User container = userRepository.findById("MAIN_USER_CONTAINER").orElse(new User());
        container.setId("MAIN_USER_CONTAINER");

        String doctorEmail = "dr.janembabazi123@gmail.com";

        boolean doctorExists = container.getDoctors().stream()
                .anyMatch(d -> d.getEmail().equals(doctorEmail));
        if (doctorExists) return;

        DoctorProfile doctorProfile = new DoctorProfile();
        doctorProfile.setFirstName("Jane");
        doctorProfile.setLastName("Mbabazi");
        doctorProfile.setEmail(doctorEmail);
        doctorProfile.setPhoneNumber("0788000002");
        doctorProfile.setPassword(passwordEncoder.encode("123456"));
        doctorProfile.setDoctorSpecialization("Cardiology");
        doctorProfile.setDoctorLicenseNumber("DOC-987654");

        container.getDoctors().add(doctorProfile);
        userRepository.save(container);

        logger.info("Doctor " + doctorProfile.getFirstName() + " seeded successfully.");
    }
}
