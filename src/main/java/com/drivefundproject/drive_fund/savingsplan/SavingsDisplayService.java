package com.drivefundproject.drive_fund.savingsplan;

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.model.Frequency;
import com.drivefundproject.drive_fund.model.SavingsPlan;
import com.drivefundproject.drive_fund.repository.SavingsPlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavingsDisplayService {

    private final SavingsPlanRepository savingsPlanRepository;

    public List<SavingsPlan>getSavingsPlanByUserId(Integer userId){
        return savingsPlanRepository.findByUserId(userId);
    }
    public double calculateExpectedPayment(SavingsPlan savingsPlan){
        double totalAmount = savingsPlan.getAmount();
        int timelineInMonths = savingsPlan.getTimeline();
        Frequency frequency = savingsPlan.getFrequency();
        long numberOfPeriods;

        if(frequency == Frequency.DAILY){
            numberOfPeriods = savingsPlan.getCreationDate().until(savingsPlan.getTargetCompletionDate(),ChronoUnit.DAYS);
        }
        else if(frequency == Frequency.WEEKLY){
            numberOfPeriods = savingsPlan.getCreationDate().until(savingsPlan.getTargetCompletionDate(),ChronoUnit.WEEKS);
        }
        else if(frequency == Frequency.MONTHLY){
            numberOfPeriods = savingsPlan.getCreationDate().until(savingsPlan.getTargetCompletionDate(),ChronoUnit.MONTHS);
        }
        else {
            //If we have an expired period or if there is an error with the dates
            //It won't calculate anything
            return 0.0;
        }
        //this helps us handle misconfigured dates/start period that is same as end date
        if(numberOfPeriods <= 0){
            return totalAmount;
        }

        return totalAmount/numberOfPeriods;

    }
    
}
