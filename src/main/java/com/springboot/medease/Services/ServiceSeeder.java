package com.springboot.medease.Services;

import com.springboot.medease.Models.Clinic;
import com.springboot.medease.Repository.ClinicRepository;
import com.springboot.medease.Repository.ServiceRepository;
import lombok.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class ServiceSeeder implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(ServiceSeeder.class.getName());

    private final ClinicRepository clinicRepository;
    private final ServiceRepository serviceRepository;

    public ServiceSeeder(ClinicRepository clinicRepository, ServiceRepository serviceRepository) {
        this.clinicRepository = clinicRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    public void run(@NonNull String... args) {
        // if services already exist, don't seed again
        if (serviceRepository.count() > 0) {
            return;
        }

        // pick any existing clinic (e.g., the one seeded by ClinicSeeder)
        Clinic clinic = clinicRepository.findAll().stream().findFirst().orElse(null);
        if (clinic == null) {
            logger.warning("No clinics found; skipping service seeding.");
            return;
        }

        com.springboot.medease.Models.Service s1 = new com.springboot.medease.Models.Service();
        s1.setName("Consultation");
        s1.setClinicId(clinic.getId());

        com.springboot.medease.Models.Service s2 = new com.springboot.medease.Models.Service();
        s2.setName("Lab Test");
        s2.setClinicId(clinic.getId());

        serviceRepository.save(s1);
        serviceRepository.save(s2);

        logger.info("Services seeded for clinicId=" + clinic.getId());
    }
}