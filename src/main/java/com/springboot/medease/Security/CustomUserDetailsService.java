package com.springboot.medease.Security;

import com.springboot.medease.Models.User;
import com.springboot.medease.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {

        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrPhone) throws UsernameNotFoundException {

        User user = userRepo.findByEmail(usernameOrPhone)
                .orElseGet(() -> userRepo.findByPhoneNumber(usernameOrPhone).orElse(null));

        if (user == null) throw new UsernameNotFoundException("User not found");

        String role = user.getUserType() != null ? user.getUserType().name() : "ROLE_PATIENT";
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail() != null ? user.getEmail() : user.getPhoneNumber())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority(role))
                .build();
    }
}

