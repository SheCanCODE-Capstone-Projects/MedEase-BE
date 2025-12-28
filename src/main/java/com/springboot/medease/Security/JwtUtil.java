package com.springboot.medease.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

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

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
