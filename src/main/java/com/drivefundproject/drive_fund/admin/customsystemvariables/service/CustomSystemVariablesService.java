package com.drivefundproject.drive_fund.admin.customsystemvariables.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.admin.customsystemvariables.dto.response.CustomSystemVariablesResponse;
import com.drivefundproject.drive_fund.admin.customsystemvariables.model.CustomSystemVariables;
import com.drivefundproject.drive_fund.admin.customsystemvariables.repository.CustomSystemVariablesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomSystemVariablesService {

    private final CustomSystemVariablesRepository customSystemVariablesRepository;

    public BigDecimal getSystemVariable(String key, BigDecimal defaultValue){
        return customSystemVariablesRepository.findTopByInterestNameOrderByCreatedAtDesc(key)
              .map(CustomSystemVariables::getInterestValue)
              .orElse(defaultValue);
    }

    public CustomSystemVariables saveOrUpdateVariable(String interestName, BigDecimal interestValue){
      CustomSystemVariables variable = customSystemVariablesRepository.findTopByInterestNameOrderByCreatedAtDesc(interestName)
           .orElse(new CustomSystemVariables());
      variable.setInterestName(interestName);
      variable.setInterestValue(interestValue);

      return customSystemVariablesRepository.save(variable);

    }
   public List<CustomSystemVariablesResponse> getAllSystemVariables() {
    // 1. Retrieve all entities from the repository
    List<CustomSystemVariables> variables = customSystemVariablesRepository.findAll();

    // 2. Map/Convert the Entity list to the DTO list
    List<CustomSystemVariablesResponse> responseList = variables.stream()
        .map(variable -> (CustomSystemVariablesResponse) CustomSystemVariablesResponse.builder()
            .interestName(variable.getInterestName())
            .interestValue(variable.getInterestValue()) 
            .createdAt(variable.getCreatedAt().toLocalDate())
            .build())
        .collect(Collectors.toList());
        
    // 3. Return the DTO list
    return responseList;
}
    
}
