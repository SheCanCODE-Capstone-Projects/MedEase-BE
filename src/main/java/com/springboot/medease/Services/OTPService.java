package com.springboot.medease.Services;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {

    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    private static class OtpEntry {
        String identifier;
        Instant expiresAt;

        OtpEntry(String identifier, Instant expiresAt) {
            this.identifier = identifier;
            this.expiresAt = expiresAt;
        }
    }

    // Generate a 6-digit OTP
    public String generateOtp(String identifier) {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        Instant expiresAt = Instant.now().plusSeconds(120);
        otpStore.put(String.valueOf(otp), new OtpEntry(identifier, expiresAt));
        return String.valueOf(otp);
    }

    // Validate OTP
    public String validateOtp(String otp) {
        OtpEntry entry = otpStore.get(otp);
        if (entry == null) return null;

        if (Instant.now().isAfter(entry.expiresAt)) {
            otpStore.remove(otp);
            return null;
        }

        otpStore.remove(otp);
        return entry.identifier;
    }
}

