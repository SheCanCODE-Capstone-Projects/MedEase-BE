package com.springboot.medease.Models;

import lombok.Data;

import java.util.Date;

@Data
public class PersonalInfo {
    private String name;
    private String phone;
    private String email;
    private Date dateOfBirth;
    private String gender;
    private String insuranceProvider;

}
