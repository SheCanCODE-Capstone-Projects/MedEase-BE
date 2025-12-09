package com.springboot.medease.Security;

import com.mongodb.lang.NonNull;
import com.springboot.medease.Models.*;
import com.springboot.medease.Repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private static final String CONTAINER_ID = "MAIN_USER_CONTAINER";

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String identifier) throws UsernameNotFoundException {

        User container = userRepository.findById(CONTAINER_ID)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Profile profile ;
        String role ;

        List<PatientProfile> patients = container.getPatients() != null ? container.getPatients() : List.of();
        profile = patients.stream()
                .filter(p -> p.getEmail().equals(identifier) || p.getPhoneNumber().equals(identifier))
                .findFirst().orElse(null);
        role = profile != null ? "ROLE_PATIENT" : null;

        if (profile == null) {
            List<DoctorProfile> doctors = container.getDoctors() != null ? container.getDoctors() : List.of();
            profile = doctors.stream()
                    .filter(d -> d.getEmail().equals(identifier) || d.getPhoneNumber().equals(identifier))
                    .findFirst().orElse(null);
            role = profile != null ? "ROLE_DOCTOR" : null;
        }

        if (profile == null) {
            List<PharmacistProfile> pharmacists = container.getPharmacists() != null ? container.getPharmacists() : List.of();
            profile = pharmacists.stream()
                    .filter(ph -> ph.getEmail().equals(identifier) || ph.getPhoneNumber().equals(identifier))
                    .findFirst().orElse(null);
            role = profile != null ? "ROLE_PHARMACIST" : null;
        }

        if (profile == null) throw new UsernameNotFoundException("User not found");

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        return new org.springframework.security.core.userdetails.User(
                profile.getEmail(),
                profile.getPassword(),
                authorities
        );

    }
}


