package com.springboot.medease.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Document(collection = "queues")
public class Queue {
    @Id
    private String id;
    
    @Field("patientId")
    @NotBlank
    @Indexed
    private String patientId;
    
    @Field("clinicId")
    @NotBlank
    @Indexed
    private String clinicId;
    
    @Field("serviceId")
    @NotBlank
    @Indexed
    private String serviceId;
    
    @Field("queuePosition")
    private int queuePosition;
    
    @Field("status")
    @NotNull
    @Indexed
    private QueueStatus status;
    
    @Field("joinTime")
    @NotNull
    @Indexed
    private LocalDateTime joinTime;
    private  String assignedDoctorId;
}