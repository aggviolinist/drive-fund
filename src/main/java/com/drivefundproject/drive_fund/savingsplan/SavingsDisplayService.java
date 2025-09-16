package com.drivefundproject.drive_fund.savingsplan;

import java.lang.StackWalker.Option;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public BigDecimal calculateInitialExpectedPayment(SavingsPlan savingsPlan){
        BigDecimal totalAmount = savingsPlan.getAmount();
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
            return BigDecimal.ZERO;
        }
        //this helps us handle misconfigured dates/start period that is same as end date
        if(numberOfPeriods <= 0){
            return totalAmount;
        }

        return totalAmount.divide(new BigDecimal (numberOfPeriods),0, RoundingMode.HALF_UP);

    }
    //Calculates new expected payment on time basis and not on period basis. Self adjust every month
    public BigDecimal calculateDynamicExpectedPayment(UUID planUuid, BigDecimal remainingAmount){
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);

        if(retrievedSavingsPlan.isPresent()){
            SavingsPlan savingsPlan = retrievedSavingsPlan.get();
            long totalPeriods;
            long elapsedPeriods;
            if(savingsPlan.getFrequency() == Frequency.DAILY){
                totalPeriods = savingsPlan.getCreationDate().until(savingsPlan.getTargetCompletionDate(),ChronoUnit.DAYS);
                elapsedPeriods = savingsPlan.getCreationDate().until(LocalDate.now(), ChronoUnit.DAYS);
            }
            else if(savingsPlan.getFrequency() == Frequency.WEEKLY){
                totalPeriods = savingsPlan.getCreationDate().until(savingsPlan.getTargetCompletionDate(), ChronoUnit.WEEKS);
                elapsedPeriods = savingsPlan.getCreationDate().until(LocalDate.now(), ChronoUnit.WEEKS);
            }
            else if(savingsPlan.getFrequency() == Frequency.MONTHLY){
                totalPeriods = savingsPlan.getCreationDate().until(savingsPlan.getTargetCompletionDate(), ChronoUnit.MONTHS);
                elapsedPeriods = savingsPlan.getCreationDate().until(LocalDate.now(), ChronoUnit.MONTHS);
            }
            else{
                return BigDecimal.ZERO;
            }
            //this is how many payments have been made
            //long paymentsMade = paymentRepository.countBySavingsPlan_PlanUuid(planUuid);

            //We need to avoid negative expected values
            if(remainingAmount.compareTo(BigDecimal.ZERO) <= 0){
                return BigDecimal.Zero;
            }
            // Remaining periods are the total periods minus the elapsedperiods
            long periodsRemaining = totalPeriods - elapsedPeriods;
            
            // Ensure periodsRemaining is not zero or negative
            if(periodsRemaining <= 0){
                return remainingAmount;//.compareTo(BigDecimal.ZERO) > 0 ? remainingAmount : BigDecimal.ZERO; //(Always returning 0)If they overpaid or are at the end, remaining amount is the last payment 
            }
            return remainingAmount.divide(new BigDecimal(periodsRemaining),0,RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;               
    }
    public SavingsProgressResponse getSavingsProgress(UUID planUuid){
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);

        if(retrievedSavingsPlan.isPresent()){
            SavingsPlan savingsPlan = retrievedSavingsPlan.get();

            BigDecimal totalDeposited = paymentService.calculateTotalDeposit(planUuid);
            BigDecimal balanceAmount = savingsPlan.getAmount().subtract(totalDeposited);
            double percentageCompleted = paymentService.calculatePercentageCompleted(planUuid);
            BigDecimal newExpectedPayment = calculateDynamicExpectedPayment(planUuid,balanceAmount);

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
