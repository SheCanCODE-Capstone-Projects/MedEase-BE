package com.springboot.medease.Security;
import com.springboot.medease.Models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private  String SECRET_KEY;

//    @PostConstruct
//    public void debugKey() {
//        System.out.println("SECRET_KEY = " + SECRET_KEY);
//    }

    @Value("${app.jwt.expiration-ms}")
    private   long EXPIRATION_TIME;

    private Key getSigningKey() {

        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
//        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
//        return Keys.hmacShaKeyFor(keyBytes);
    }

    public  String generateToken(User user) {
        System.out.println("Generating Token...");
        return Jwts.builder()
                .setSubject(user.getEmail() != null ? user.getEmail() : user.getPhoneNumber())
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .claim("phone", user.getPhoneNumber())
                .claim("userType", user.getUserType() != null ? user.getUserType().name() : null)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    public String extractPhone(String token) {
        return extractAllClaims(token).get("phone", String.class);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("userType", String.class);
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).get("id", String.class);
    }

    public boolean validateToken(String token) {
        return !extractAllClaims(token).getExpiration().before(new Date());
    }
}

