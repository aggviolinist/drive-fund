package com.drivefundproject.drive_fund.admin.customsystemvariables.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.admin.customsystemvariables.model.CustomSystemVariables;
import com.drivefundproject.drive_fund.admin.customsystemvariables.repository.CustomSystemVariablesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomSystemVariablesService {

    private final CustomSystemVariablesRepository customSystemVariablesRepository;

    public BigDecimal getInterestRate(String key, BigDecimal defaultValue){
        return customSystemVariablesRepository.findByKey(key)
              .map(CustomSystemVariables::getValue)
              .orElse(defaultValue);
    }
    
}
