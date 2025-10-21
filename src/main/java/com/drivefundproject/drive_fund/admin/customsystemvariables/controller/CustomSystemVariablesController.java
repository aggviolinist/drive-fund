package com.drivefundproject.drive_fund.admin.customsystemvariables.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drivefundproject.drive_fund.admin.customsystemvariables.dto.request.CustomSystemVariablesRequest;
import com.drivefundproject.drive_fund.admin.customsystemvariables.model.CustomSystemVariables;
import com.drivefundproject.drive_fund.admin.customsystemvariables.service.CustomSystemVariablesService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/systemvariables")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class CustomSystemVariablesController {
    private final CustomSystemVariablesService customSystemVariablesService;

    @PostMapping
    public ResponseEntity<CustomSystemVariables> updateSystemVariable(@Valid @RequestBody CustomSystemVariablesRequest customSystemVariablesRequest){
        CustomSystemVariables updatedVariables = customSystemVariablesService.saveOrUpdateVariable(
            customSystemVariablesRequest.getInterestName().toUpperCase(),
            customSystemVariablesRequest.getInterestAmount()
        );
        return ResponseEntity.ok(updatedVariables);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<CustomSystemVariables>> bulkUpdateSystemVariable (@Valid @RequestBody List<CustomSystemVariablesRequest> customSystemVariablesRequest){
        List<CustomSystemVariables> updatedVariables = 

        
    }
    
}
