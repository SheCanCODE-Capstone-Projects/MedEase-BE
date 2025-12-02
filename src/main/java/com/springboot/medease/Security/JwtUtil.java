package com.springboot.medease.Security;
import com.springboot.medease.Models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private  String SECRET_KEY;
    @Value("${app.jwt.expiration-ms}")
    private   long EXPIRATION_TIME;

    private Key getSigningKey() {

        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public  String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId())
                .claim("email", user.getEmail())
                .claim("userType", user.getUserType())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}

