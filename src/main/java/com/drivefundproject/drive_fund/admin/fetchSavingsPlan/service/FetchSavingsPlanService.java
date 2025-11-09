package com.drivefundproject.drive_fund.admin.fetchSavingsPlan.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.admin.fetchSavingsPlan.repository.FetchSavingsPlanRepository;
import com.drivefundproject.drive_fund.user.addsavingsplan.dto.response.ConciseSavingsPlanResponse;
import com.drivefundproject.drive_fund.user.addsavingsplan.model.SavingsPlan;
import com.drivefundproject.drive_fund.user.addsavingsplan.repository.SavingsPlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FetchSavingsPlanService {

    private final FetchSavingsPlanRepository fetchSavingsPlanRepository;
    private final FetchSavingsPlanMapper fetchSavingsPlanMapper; 

    public List<ConciseSavingsPlanResponse> getAllSavingsPlans() {
        
        List<SavingsPlan> allPlans = fetchSavingsPlanRepository.findAll(); 

        return allPlans.stream()
                .map(fetchSavingsPlanMapper::toConciseSavingsPlanResponse) 
                .collect(Collectors.toList());
    }
}