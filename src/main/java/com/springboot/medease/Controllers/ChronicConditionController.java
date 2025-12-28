package com.springboot.medease.Controllers;

import com.springboot.medease.DTOs.ChronicConditionRequestDTO;
import com.springboot.medease.DTOs.ChronicConditionResponseDTO;
import com.springboot.medease.Services.ChronicConditionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chronic-conditions")
public class ChronicConditionController {

    private final ChronicConditionService service;

    public ChronicConditionController(ChronicConditionService service) {
        this.service = service;
    }

    //  VIEW (Doctor + Pharmacist)
    @GetMapping("/{patientId}")
    public ResponseEntity<List<ChronicConditionResponseDTO>> view(
            @PathVariable String patientId
    ) {
        return ResponseEntity.ok(service.viewConditions(patientId));
    }

    // ADD (Doctor ONLY)
    @PostMapping("/{patientId}")
    public ResponseEntity<Void> add(
            @PathVariable String patientId,
            @RequestBody ChronicConditionRequestDTO dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        service.addCondition(patientId, dto, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // UPDATE (Doctor ONLY)
    @PutMapping("/{patientId}/{conditionId}")
    public ResponseEntity<Void> update(
            @PathVariable String patientId,
            @PathVariable String conditionId,
            @RequestBody ChronicConditionRequestDTO dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        service.updateCondition(
                patientId,
                conditionId,
                dto,
                user.getUsername()
        );
        return ResponseEntity.ok().build();
    }
}

