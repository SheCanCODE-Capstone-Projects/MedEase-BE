package com.springboot.medease.Models;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Document(collection = "consultation")
public class Consultation {

    @Id
    private String id;

    private String diagnosis;
    private String symptoms;

    private String doctorId;
    private String patientId;
    private String clinicId;



    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;


    @Field("timestamp")
    private Instant timestamp;

    // Constructor
    public Consultation() {
        this.timestamp = Instant.now();
    }


}
