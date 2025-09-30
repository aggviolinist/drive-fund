package com.drivefundproject.drive_fund.savingsplan;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import com.drivefundproject.drive_fund.dto.Response.InterestResponse;
import com.drivefundproject.drive_fund.model.SavingsPlan;
import com.drivefundproject.drive_fund.repository.SavingsPlanRepository;
import com.drivefundproject.drive_fund.repository.SavingsPlanRepository;


public class WithdrawalService {

    private final SavingsPlanRepository savingsPlanRepository;

    private final BigDecimal WITHDRAWALPENALTY_FEE = new BigDecimal("0.10");
    private final BigDecimal WITHDRAWAL_FEE = new BigDecimal("0.02");

    public void recordWithdrawal(UUID plaUuid, BigDecimal WithdrawnAmount){
        // 1. Validate Savings Plan and Status
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(plaUuid);

        if(!retrievedSavingsPlan.isPresent()){
            throw new IllegalArgumentException("Savings Plan not found");

        }


    }
    
}
