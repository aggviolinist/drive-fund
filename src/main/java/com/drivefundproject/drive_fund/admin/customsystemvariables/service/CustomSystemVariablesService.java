package com.drivefundproject.drive_fund.admin.customsystemvariables.service;

import java.math.BigDecimal;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

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
    
}
