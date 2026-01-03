package com.springboot.medease.Controllers;

import com.springboot.medease.DTOs.*;
import com.springboot.medease.Services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerPatient(@Valid @RequestBody PatientRegisterRequest request) {
        AuthResponse response = authService.registerPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/pharmacist/register")
    public ResponseEntity<AuthResponse> registerPharmacist(@Valid @RequestBody PharmacistRegisterRequest request) {
        AuthResponse response = authService.registerPharmacist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/google/login")
    public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        AuthResponse response = authService.googleLogin(request.getIdToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/send/otp")
    public ResponseEntity<String> loginAndSendOtp(@Valid @RequestBody LoginRequest request) {
        authService.sendLoginOtp(request.getIdentifier(), request.getPassword());
        return ResponseEntity.ok("OTP sent to your email");
    }

    @PostMapping("/login/verify/otp")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody OTPVerificationRequest request) {
        AuthResponse response = authService.verifyOtpAndLogin(request.getOtp());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/reset/request")
    public ResponseEntity<String> requestPasswordReset(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.sendResetPasswordLink(request.getIdentifier());
        return ResponseEntity.ok("Password reset link sent to your email");
    }

    @PostMapping("/password/reset")
    public ResponseEntity<AuthResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        AuthResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }


}


