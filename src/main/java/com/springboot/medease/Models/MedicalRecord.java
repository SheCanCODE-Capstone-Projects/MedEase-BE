package com.springboot.medease.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "medical_records")
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class MedicalRecord {

    @Id
    private String id;

    private String patientId;

    private List<ChronicCondition> chronicConditions = new ArrayList<>();

}

