package com.springboot.medease.DTOs;

import lombok.Data;

import java.util.Date;

@Data
public class PersonalInfoUpdateRequest {

    private String name;
    private String phone;
    private String email;
    private Date dateOfBirth;
    private String gender;
    private String insuranceProvider;

}
