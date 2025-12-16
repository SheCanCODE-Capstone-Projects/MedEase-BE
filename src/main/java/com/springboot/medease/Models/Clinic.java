package com.springboot.medease.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;

@Data
@Document(collection = "clinics")
public class Clinic {
    @Id
    private String id;
    
    @Field("name")
    @NotBlank
    private String name;
    
    @Field("location")
    @NotBlank
    private String location;
}