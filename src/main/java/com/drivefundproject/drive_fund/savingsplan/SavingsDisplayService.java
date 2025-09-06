package com.drivefundproject.drive_fund.savingsplan;

import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.repository.SavingsPlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavingsDisplayService {

    private final SavingsPlanRepository savingsPlanRepository;

    public List<SavingsPlan>getSavingsDisplayService(Integer userId){
        return savingsPlanRepository.findById(userId);
    }
    
}
