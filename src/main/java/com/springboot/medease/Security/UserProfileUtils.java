package com.springboot.medease.Security;


import com.springboot.medease.Models.DoctorProfile;
import com.springboot.medease.Models.PatientProfile;
import com.springboot.medease.Models.PharmacistProfile;

public class UserProfileUtils {

    public static ProfileInfo extractProfileInfo(Object profile, String role) {
        if (profile == null) return null;

        if (profile instanceof PatientProfile p) {
            return new ProfileInfo(p.getEmail(), p.getPhoneNumber(), p.getPassword(), role);
        } else if (profile instanceof DoctorProfile d) {
            return new ProfileInfo(d.getEmail(), d.getPhoneNumber(), d.getPassword(), role);
        } else if (profile instanceof PharmacistProfile ph) {
            return new ProfileInfo(ph.getEmail(), ph.getPhoneNumber(), ph.getPassword(), role);
        }

        return null;
    }

    public record ProfileInfo(String email, String phone, String password, String role) {}
}

