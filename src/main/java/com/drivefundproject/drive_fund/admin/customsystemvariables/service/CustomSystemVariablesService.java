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

    // @param
    // @param
    // @return
    public BigDecimal getSystemVariable(String key, BigDecimal defaultValue){
        return customSystemVariablesRepository.findByInterestName(key)
              .map(CustomSystemVariables::getInterestValue)
              .orElse(defaultValue);
    }

    // @param
    // @param
    // @return
    public CustomSystemVariables saveOrUpdateVariable(String interestName, BigDecimal interestValue){
      CustomSystemVariables variable = customSystemVariablesRepository.findByInterestName(interestName)
           .orElse(new CustomSystemVariables());
      variable.setInterestName(interestName);
      variable.setInterestValue(interestValue);

      return customSystemVariablesRepository.save(variable);

    }
    
}
