package com.drivefundproject.drive_fund.admin.customsystemvariables.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.drivefundproject.drive_fund.admin.customsystemvariables.model.CustomSystemVariables;

public interface CustomSystemVariablesRepository extends JpaRepository <CustomSystemVariables, Long> {

    Optional<CustomSystemVariables> findByInterestName(String InterestName);
    
}
