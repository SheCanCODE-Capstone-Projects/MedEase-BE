package com.springboot.medease.Controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Value("${spring.application.name:MedEase}")
    private String applicationName;

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", applicationName);
        health.put("timestamp", LocalDateTime.now());
        health.put("message", "MedEase Backend is running successfully");
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "HEALTHY");
        status.put("service", "MedEase Backend API");
        
        return ResponseEntity.ok(status);
    }
}