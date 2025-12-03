package com.springboot.medease.Controllers;

import com.springboot.medease.DTOs.PharmacistRegisterRequest;
import com.springboot.medease.DTOs.pharmacistRegisterResponse;
import com.springboot.medease.Services.pharmacistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pharmacist")
public class PharmacyController {

    @Autowired
    private pharmacistService pharmacistService;

    @PostMapping("/register")
    public ResponseEntity<pharmacistRegisterResponse> registerPharmacy(
            @Valid @RequestBody PharmacistRegisterRequest request) {

        pharmacistRegisterResponse response = pharmacistService.registerPharmacist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

