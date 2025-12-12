package com.springboot.medease.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;

@Data
@Document(collection = "services")
public class Service {
    @Id
    private String id;
    
    @Field("name")
    @NotBlank
    private String name;
    
    @Field("clinicId")
    @NotBlank
    private String clinicId;
}