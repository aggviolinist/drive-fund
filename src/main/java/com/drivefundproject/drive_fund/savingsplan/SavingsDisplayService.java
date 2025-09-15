package com.drivefundproject.drive_fund.savingsplan;

import java.lang.StackWalker.Option;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.model.Frequency;
import com.drivefundproject.drive_fund.model.Payment;
import com.drivefundproject.drive_fund.model.SavingsPlan;
import com.drivefundproject.drive_fund.repository.PaymentRepository;
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
    public double calculateExpectedPaymentBasedOnRemainingBalance(UUID planUuid, double remainingAmount){
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);

        if(retrievedSavingsPlan.isPresent()){
            SavingsPlan savingsPlan = retrievedSavingsPlan.get();

            //1. Find the most recent payment date
            //This method will be implemented in PaymentRepository
            

            //2. Set start date for calculation based on payment history
            LocalDate startDateForCalculation;
            if(latestPayment.isPresent()){
                startDateForCalculation = latestPayment.get().getPaymentDate();
            }
            else{
                //if no payments have been made, use the plan's creation date
                startDateForCalculation = savingsPlan.getCreationDate();
            }
            
            //3. Calculate remaining periods using determined start date
            long periodsRemaining;
            if(savingsPlan.getFrequency() == Frequency.DAILY){
                periodsRemaining = startDateForCalculation.until(savingsPlan.getTargetCompletionDate(),ChronoUnit.DAYS);
            }
            else if(savingsPlan.getFrequency() == Frequency.WEEKLY){
                periodsRemaining = startDateForCalculation.until(savingsPlan.getTargetCompletionDate(), ChronoUnit.WEEKS);
            }
            else if(savingsPlan.getFrequency() == Frequency.MONTHLY){
                periodsRemaining = startDateForCalculation.until(savingsPlan.getTargetCompletionDate(), ChronoUnit.MONTHS);
            }
            else{
                return 0.0;
            }
            return remainingAmount/periodsRemaining;
        }
        //if savings plan is not found, return 0.0
        return 0.0;
        
        // //Last payment date needs to calculate remaining periods
        // LocalDate startDateForCalculation = lastPaymentDate != null ? setPaymentDate()
        // long periodsRemaining;
        // if(savingsPlan.getFrequency() == Frequency.DAILY){
        //     periodsRemaining = LocalDate.now().until(savingsPlan.getTargetCompletionDate(), null)
        // }

    }
    
}
