package com.drivefundproject.drive_fund.savingsplan;

import java.lang.StackWalker.Option;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.dto.Response.SavingsProgressResponse;
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
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public List<SavingsPlan>getSavingsPlanByUserId(Integer userId){
        return savingsPlanRepository.findByUserId(userId);
    }
    public double calculateInitialExpectedPayment(SavingsPlan savingsPlan){
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
    public double calculateDynamicExpectedPayment(UUID planUuid, double remainingAmount){
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);

        if(retrievedSavingsPlan.isPresent()){
            SavingsPlan savingsPlan = retrievedSavingsPlan.get();
            //Wrong approach, I used time instead of periods
            // if(latestPayment.isPresent()){
            //     startDateForCalculation = latestPayment.get().getPaymentDate();
            // }
            // else{
            //     //if no payments have been made, use the plan's creation date
            //     startDateForCalculation = savingsPlan.getCreationDate();
            // }            
            //Calculate total number of periods in plan's lifetime
            long totalPeriods;
            if(savingsPlan.getFrequency() == Frequency.DAILY){
                totalPeriods = savingsPlan.getCreationDate().until(savingsPlan.getTargetCompletionDate(),ChronoUnit.DAYS);
            }
            else if(savingsPlan.getFrequency() == Frequency.WEEKLY){
                totalPeriods = savingsPlan.getCreationDate().until(savingsPlan.getTargetCompletionDate(), ChronoUnit.WEEKS);
            }
            else if(savingsPlan.getFrequency() == Frequency.MONTHLY){
                totalPeriods = savingsPlan.getCreationDate().until(savingsPlan.getTargetCompletionDate(), ChronoUnit.MONTHS);
            }
            else{
                return 0.0;
            }
            //this is how many payments have been made
            long paymentsMade = paymentRepository.countBySavingsPlan_PlanUuid(planUuid);

            // Remaining periods are the total periods minus payments made
            long periodsRemaining = totalPeriods - paymentsMade;
            
            // Ensure periodsRemaining is not zero or negative
            if(periodsRemaining <= 0){
                return remainingAmount > 0 ? remainingAmount : 0.0; //If they overpaid or are at the end, remaining amount is the last payment
            }
            return remainingAmount/periodsRemaining;
        }
        return 0.0;                
    }
    public SavingsProgressResponse getSavingsProgress(UUID planUuid){
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);

        if(retrievedSavingsPlan.isPresent()){
            SavingsPlan savingsPlan = retrievedSavingsPlan.get();

            Double totalDeposited = paymentService.calculateTotalDeposit(planUuid);
            Double balanceAmount = savingsPlan.getAmount() - totalDeposited;
            double percentageCompleted = paymentService.calculatePercentageCompleted(planUuid);
            double newExpectedPayment = calculateDynamicExpectedPayment(planUuid,balanceAmount);

            //this is the dynamic expected payment using remaining amount
            return new SavingsProgressResponse(
                savingsPlan.getPlanUuid(),
                savingsPlan.getCatalogue().getProductname(),
                savingsPlan.getAmount(),
                totalDeposited,
                balanceAmount,
                newExpectedPayment,
                Math.min(percentageCompleted,100.0)
                );

        }
        throw new IllegalArgumentException("Savings Plan not found");

    }
    
}
