package com.springboot.medease.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Document(collection = "queues")
@CompoundIndexes({
    @CompoundIndex(
        name = "idx_clinic_service_status_jointime",
        def = "{'clinicId': 1, 'serviceId': 1, 'status': 1, 'joinTime': 1}"
    ),
    @CompoundIndex(
        name = "idx_doctor_status",
        def = "{'assignedDoctorId': 1, 'status': 1}"
    ),
    @CompoundIndex(
        name = "idx_patient_status",
        def = "{'patientId': 1, 'status': 1}"
    )
})
public class Queue {
    @Id
    private String id;

    @Version
    private Long version;

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