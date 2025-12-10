package com.springboot.medease.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import com.springboot.medease.Models.*;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    @Value("${app.jwt.expiration-ms}")
    private long EXPIRATION_TIME;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
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
                .claim("role", userType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
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



