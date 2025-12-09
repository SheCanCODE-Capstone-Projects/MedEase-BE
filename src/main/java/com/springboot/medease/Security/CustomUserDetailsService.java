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

        Object profile ;
        String role ;

        profile = container.getPatients().stream()
                .filter(p -> p.getEmail().equals(identifier) || p.getPhoneNumber().equals(identifier))
                .findFirst().orElse(null);
        role = profile != null ? "ROLE_PATIENT" : null;

        if (profile == null) {
            profile = container.getDoctors().stream()
                    .filter(d -> d.getEmail().equals(identifier) || d.getPhoneNumber().equals(identifier))
                    .findFirst().orElse(null);
            role = profile != null ? "ROLE_DOCTOR" : null;
        }

        if (profile == null) {
            profile = container.getPharmacists().stream()
                    .filter(ph -> ph.getEmail().equals(identifier) || ph.getPhoneNumber().equals(identifier))
                    .findFirst().orElse(null);
            role = profile != null ? "ROLE_PHARMACIST" : null;
        }

        if (profile == null) throw new UsernameNotFoundException("User not found");

        String password = null;
        if (profile instanceof PatientProfile p) password = p.getPassword();
        if (profile instanceof DoctorProfile d) password = d.getPassword();
        if (profile instanceof PharmacistProfile ph) password = ph.getPassword();

        if (profile instanceof PharmacistProfile) role = "ROLE_PHARMACIST";
        else if (profile instanceof DoctorProfile) role = "ROLE_DOCTOR";
        else role = "ROLE_PATIENT";


        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        return new org.springframework.security.core.userdetails.User(
                ((Profile) profile).getEmail(),
                ((Profile) profile).getPassword(),
                new ArrayList<>()
        );

    }
}


