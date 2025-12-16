package com.springboot.medease.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "consultations")
public class Consultation {

    @Id
    private String id;

    private String diagnosis;
    private String symptoms;

    private String doctorId;
    private String patientId;
    private String clinicId;


    private LocalDateTime timestamp = LocalDateTime.now();
}
