package com.springboot.medease.Services;

import com.springboot.medease.Models.Clinic;
import com.springboot.medease.Repository.ClinicRepository;
import lombok.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class ClinicSeeder implements CommandLineRunner {

    private  static final Logger logger = Logger.getLogger(ClinicSeeder.class.getName());

    private final ClinicRepository clinicRepository;

    public ClinicSeeder(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    @Override
    public void run(@NonNull String... args) {
        // Seed only if there are no clinics yet
        if (clinicRepository.count() > 0) {
            return;
        }

        Clinic clinic = new Clinic();
        clinic.setName("Main Clinic");
        clinic.setLocation("car-free-zone");

        clinicRepository.save(clinic);

        logger.info("Clinic seeded successfully: " + clinic.getName() + " (" + clinic.getLocation() + ")");
    }
}