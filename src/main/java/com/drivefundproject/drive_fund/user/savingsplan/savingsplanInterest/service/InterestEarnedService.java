package com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.user.addsavingsplan.model.SavingsPlan;
import com.drivefundproject.drive_fund.user.addsavingsplan.repository.SavingsPlanRepository;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.dto.response.InterestResponse;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.model.InterestEarned;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.model.InterestType;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.repository.InterestEarnedRepository;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.model.Payment;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterestEarnedService {

    private final SavingsPlanRepository savingsPlanRepository;
    private final InterestEarnedRepository interestEarnedRepository;
    private final PaymentRepository paymentRepository;

    //private final BigDecimal FIVE_PERCENT_INTEREST_RATE = new BigDecimal("0.05"); //5% interest on 50% deposit
    //private final BigDecimal SEVEN_POINT_FIVE_PERCENT_INTEREST_RATE = new BigDecimal("0.075"); //7.5% interest on 75% deposit

   private BigDecimal getFivePercentInterestRate(){
    return customSystemVariableService.getInterestRate("INTEREST_50_PERCENT", null);
   }
   private BigDecimal getSevenPointFivePercentInterestRate(){
    return customSystemVariableService.getInterestRate("INTEREST_75_PERCENT", null);
   } 

   BigDecimal fivePercentRate = getFivePercentInterestRate();
   BigDecimal sevenPointFivePercentRate = getSevenPointFivePercentInterestRate();

    //1. CHECK IF INTEREST HAS BEEN EARNED
    public boolean hasInterestBeenAwarded(UUID planUuid, InterestType interestType){
        //Has user been awarded interest?
        long count = interestEarnedRepository.countBySavingsPlan_PlanUuidAndInterestType(planUuid, interestType);
        return count > 0;
    }
    //2. CALCULATE INTEREST 50% AND 75% 
    public InterestResponse calculateAndApplyInterest(UUID planUuid, double percentageCompleted){
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);

        if(!retrievedSavingsPlan.isPresent()){
            //Empty response when plan isn't found
            return new InterestResponse(planUuid, BigDecimal.ZERO, BigDecimal.ZERO, 0.0, BigDecimal.ZERO, "Savings Plan not found!");
        }

        SavingsPlan savingsPlan =  retrievedSavingsPlan.get();
        BigDecimal targetAmount = savingsPlan.getAmount();
        BigDecimal paidTillToday = paymentRepository.findBySavingsPlan_PlanUuidOrderByPaymentDateAsc(planUuid) //Fetch all payments for a given plan, already sorted by date.
                    .stream() 
                    .map(Payment::getPaymentAmount)//Extracts just the paymentAmount from each payment.
                    .reduce(BigDecimal.ZERO,BigDecimal::add)//Sums all payment amounts
                    .add(calculateTotalInterest(planUuid));//Adds any previously awarded interest from your InterestEarnedRepository

                    
        BigDecimal interestAmount = BigDecimal.ZERO;
        String message = "No interest Awarded!";
        InterestType interestType = null;

        //a. CHECK 75% THRESHOLD
        if(percentageCompleted >= 75.00 && !hasInterestBeenAwarded(planUuid, InterestType.INTEREST_75_PERCENT)){
            interestAmount = targetAmount.multiply(sevenPointFivePercentRate);
            interestType = InterestType.INTEREST_75_PERCENT;
            message = "Congratulations! 75% target reached. " + sevenPointFivePercentRate.multiply(new BigDecimal("100")) + "% interest awarded.";

            //ADD 7.5% INTEREST AS PAYMENT
            applyInterestAsPayment(savingsPlan, interestAmount, interestType);
            //ADD 7.5% INTEREST TO THE TOTAL DEPOSIT PAID SO FAR
            paidTillToday = paidTillToday.add(interestAmount);

        }
        //b. CHECK 50% THRESHOLD
        else if(percentageCompleted >= 50.00 && ! hasInterestBeenAwarded(planUuid, InterestType.INTEREST_50_PERCENT)){
            interestAmount = targetAmount.multiply(fivePercentRate);
            interestType = InterestType.INTEREST_50_PERCENT;
            message = "Congratulations! 50% target reached. " + fivePercentRate.multiply(new BigDecimal("100")) + "% interest awarded.";

            //ADD 5% INTEREST EARNED AS PAYMENT
            applyInterestAsPayment(savingsPlan, interestAmount, interestType);
            //ADD 5% INTEREST EARNED TO THE TOTAL DEPOSIT PAID SO FAR
            paidTillToday = paidTillToday.add(interestAmount);
        }
            
    return new InterestResponse(
        planUuid,
        targetAmount,
        paidTillToday, 
        percentageCompleted, 
        interestAmount.setScale(2, RoundingMode.HALF_UP), 
        message
    );

    }
    private InterestEarned applyInterestAsPayment(SavingsPlan savingsPlan, BigDecimal interestAmount, InterestType interestType){
        if(interestAmount.compareTo(BigDecimal.ZERO) > 0){
            InterestEarned interestEarned = new InterestEarned();

            interestEarned.setSavingsPlan(savingsPlan);
            interestEarned.setInterestAmount(interestAmount);
            interestEarned.setDateInterestEarned(LocalDate.now());
            interestEarned.setInterestType(interestType);
            interestEarned.setTransactionId("INTEREST-" + interestType.name() + "-" + UUID.randomUUID().toString().substring(0,8));

            return interestEarnedRepository.save(interestEarned);
        }
        else{
            throw new IllegalArgumentException("Can't apply interest/Interest amount is zero.");
        }
    }
    public BigDecimal calculateTotalInterest(UUID planUuid){
         List<InterestEarned> interests = interestEarnedRepository.findBySavingsPlan_PlanUuid(planUuid);
             return interests.stream()
                       .map(InterestEarned::getInterestAmount)
                       .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



    
}
