package com.drivefundproject.drive_fund.admin.customsystemvariables.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drivefundproject.drive_fund.admin.customsystemvariables.dto.request.CustomSystemVariablesRequest;
import com.drivefundproject.drive_fund.admin.customsystemvariables.dto.response.CustomSystemVariablesResponse;
import com.drivefundproject.drive_fund.admin.customsystemvariables.model.CustomSystemVariables;
import com.drivefundproject.drive_fund.admin.customsystemvariables.service.CustomSystemVariablesService;
import com.drivefundproject.drive_fund.exception.ResponseHandler;
import com.drivefundproject.drive_fund.profile.dto.response.UserProfileResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/systemvariables")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class CustomSystemVariablesController {
    private final CustomSystemVariablesService customSystemVariablesService;

    @PostMapping
    public ResponseEntity<CustomSystemVariables> updateSingleSystemVariable(@Valid @RequestBody CustomSystemVariablesRequest customSystemVariablesRequest){
        CustomSystemVariables updatedVariables = customSystemVariablesService.saveOrUpdateVariable(
            customSystemVariablesRequest.getInterestName().toUpperCase(),
            new BigDecimal(customSystemVariablesRequest.getInterestValue())
        );
        return ResponseEntity.ok(updatedVariables);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<CustomSystemVariables>> updateBulkSystemVariable (@Valid @RequestBody List<CustomSystemVariablesRequest> customSystemVariablesRequest){
        List<CustomSystemVariables> updatedVariables = customSystemVariablesRequest.stream()
             .map(request -> customSystemVariablesService.saveOrUpdateVariable(
                request.getInterestName().toUpperCase(),
                new BigDecimal(request.getInterestValue())
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(updatedVariables);

        
    }
    @GetMapping("/getsystemvariables") 
    public ResponseEntity<Object> getAllSystemVariables(){
            List<CustomSystemVariablesResponse> response = customSystemVariablesService.getAllSystemVariables();
    return ResponseHandler.generateResponse(HttpStatus.OK, "System Variables fetched successfully", response);
}
    

    
    
}
