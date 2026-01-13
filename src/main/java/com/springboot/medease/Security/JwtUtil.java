package com.springboot.medease.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import com.springboot.medease.Models.*;

@Component
public class JwtUtil {
    
    private final SecretKey key;
    private final long expiration;
    
    public JwtUtil(@Value("${app.jwt.secret:}") String jwtSecret,
                   @Value("${app.jwt.expiration-ms:86400000}") long expiration
                   ) {
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT secret must be provided via jwt.secret property or JWT_SECRET environment variable");
        }
        if (jwtSecret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
        }

        if (expiration <= 0) {
             throw new IllegalArgumentException("JWT expiration must be greater than 0");
               }
        if (expiration > 31536000000L) { // 365 days in ms
            throw new IllegalArgumentException("JWT expiration should not exceed 365 days for security reasons");
              }
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.expiration = expiration;
    }

    private Key getSigningKey() {
        return key;
    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public String generateToken(String username) {
        return generateToken(username, "ROLE_USER");
    }

    public String generateToken(Object profile, UserType userType) {
        if (profile == null) {
                    throw new IllegalArgumentException("Profile cannot be null");
        }
        String email , phone ;

        if (profile instanceof PatientProfile p) {
            email = p.getEmail();
            phone = p.getPhoneNumber();
        } else if (profile instanceof DoctorProfile d) {
            email = d.getEmail();
            phone = d.getPhoneNumber();
        } else if (profile instanceof PharmacistProfile ph) {
            email = ph.getEmail();
            phone = ph.getPhoneNumber();
        } else {
                  throw new IllegalArgumentException("Unsupported profile type: " + profile.getClass().getName());
        }

        String subject = email != null ? email : phone;

        if (subject == null) {
                  throw new IllegalArgumentException("Profile must have either email or phone number");
        }
        return Jwts.builder()
                .setSubject(subject)
                .claim("role", userType.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean validateToken(String token) {
        return !extractAllClaims(token).getExpiration().before(new Date());
    }
}



