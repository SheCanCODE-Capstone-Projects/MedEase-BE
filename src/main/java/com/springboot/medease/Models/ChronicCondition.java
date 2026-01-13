package com.springboot.medease.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
@Data
public class ChronicCondition {
    @Id
    private String id;
    private String name;
    private String type;
    private String diagnosedByDoctorId;
    private LocalDate diagnosedDate;
    private int version;
    private boolean active;
}
