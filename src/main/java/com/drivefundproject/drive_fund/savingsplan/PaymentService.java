package com.drivefundproject.drive_fund.savingsplan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.dto.Response.InterestResponse;
import com.drivefundproject.drive_fund.model.Frequency;
import com.drivefundproject.drive_fund.model.Payment;
import com.drivefundproject.drive_fund.model.PaymentType;
import com.drivefundproject.drive_fund.model.SavingsPlan;
import com.drivefundproject.drive_fund.model.Status;
import com.drivefundproject.drive_fund.repository.PaymentRepository;
import com.drivefundproject.drive_fund.repository.SavingsPlanRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class PaymentService {
    private final SavingsPlanRepository savingsPlanRepository;
    private final PaymentRepository paymentRepository;

    private final BigDecimal FIVE_PERCENT_INTEREST_FEE =  new BigDecimal("0.05"); //5% interest on 50% deposit
    private final BigDecimal SEVENPOINTFIVE_PERCENT_INTEREST_FEE = new BigDecimal("0.075"); //7.5% interest on 75% deposit

    public Payment recordPaymentDeposit( UUID planUuid, BigDecimal paymentAmount){
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);
        //We want to avoid overpayment of the target amount
        if(retrievedSavingsPlan.isPresent()){
            SavingsPlan savingsPlan = retrievedSavingsPlan.get();

            BigDecimal currentTotalDeposited = calculateTotalDeposit(planUuid);
            BigDecimal newTotalDeposited = currentTotalDeposited.add(paymentAmount);
            BigDecimal targetAmount = savingsPlan.getAmount();

            if(newTotalDeposited.compareTo(targetAmount) > 0){
                throw new IllegalArgumentException("Payment amount exceeds the remaining savings goal. The remaining amount is:" + targetAmount.subtract(currentTotalDeposited));

            }
            //IN-PROGRESS Status Logic
            if(savingsPlan.getStatus() == Status.PENDING){
                savingsPlan.setStatus(Status.IN_PROGRESS);
                savingsPlanRepository.save(savingsPlan);
            }

            //Adding new payment
            Payment payment = new Payment();
            payment.setSavingsPlan(savingsPlan);
            payment.setAmount(paymentAmount);
            payment.setPaymentType(PaymentType.DEPOSIT); //Deposit interest as payment
            payment.setPaymentDate(LocalDate.now());

            Payment savedPayment = paymentRepository.save(payment);

            //COMPLETE status logic
            BigDecimal updatedTotalDeposits = calculateTotalDeposit(planUuid);
            if(updatedTotalDeposits.compareTo(targetAmount) >=0 ){
                savingsPlan.setStatus(Status.COMPLETED);
                savingsPlanRepository.save(savingsPlan);
            }
            return savedPayment;
        }
        else{
            throw new IllegalArgumentException("Savings Plan not found");
        }
    }

    //1. CHECK IF INTEREST HAS BEEN AWARDED
    public boolean hasInterestBeenAwarded(UUID planUuid, PaymentType paymentType){
        //This count is in my repo, and helps me find if user has been awarded the interest already
        //If in this planuuid, there is any interest awarded count;
        long count =  paymentRepository.countBySavingsPlan_PlanUuidAndPaymentType(planUuid,paymentType);
        return count > 0;
    }

    //2. CALCULATING INTEREST 50% AND 75% INTEREST
    public InterestResponse calculateInterest(UUID planUuid, double percentageCompleted){
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);
         
        if(!retrievedSavingsPlan.isPresent()){
            //Empty response when plan isn't found
            return new InterestResponse(planUuid, BigDecimal.ZERO, BigDecimal.ZERO, 0.0, BigDecimal.ZERO, "Savings Plan not found!");
        }
        //Initialize before doing the interest math
        SavingsPlan savingsPlan = retrievedSavingsPlan.get();
        BigDecimal targetAmount = savingsPlan.getAmount();
        BigDecimal paidTillToday = calculateTotalDeposit(planUuid);
        BigDecimal interestAmount = BigDecimal.ZERO;
        String message = "No interest awarded.";
        PaymentType interestType = null;

        //a. CHECK 50% THRESHOLD
        if(percentageCompleted >= 50.00 && !hasInterestBeenAwarded(planUuid, PaymentType.INTEREST_50_PERCENT)){
            interestAmount = targetAmount.multiply(FIVE_PERCENT_INTEREST_FEE);
            interestType = PaymentType.INTEREST_50_PERCENT;
            message = "Congratulations! 50% target reached. " + FIVE_PERCENT_INTEREST_FEE.multiply(new BigDecimal("100")) + "% interest awarded."; 
        }
        //b. CHECK 75% THRESHOLD
        else if(percentageCompleted >= 75.00 && !hasInterestBeenAwarded(planUuid, PaymentType.INTEREST_75_PERCENT)){
            interestAmount = targetAmount.multiply(SEVENPOINTFIVE_PERCENT_INTEREST_FEE);
            interestType = PaymentType.INTEREST_75_PERCENT;
            message = "Congratulations! 75% target reached. " + SEVENPOINTFIVE_PERCENT_INTEREST_FEE.multiply(new BigDecimal("100")) + "% interest awarded.";
        }
        //3. IF INTEREST WAS AWARDED ADD IT AS PAYMENT
        if(interestAmount.compareTo(BigDecimal.ZERO) > 0){
            applyInterestAsPayment(planUuid, interestAmount, interestType);

        //4. ADD INTEREST TO THE TOTAL DEPOSIT JUST EARNED
            paidTillToday = calculateTotalDeposit(planUuid);
        }
        return new InterestResponse(
            planUuid,
            targetAmount,
            paidTillToday,
            percentageCompleted,
            interestAmount.setScale(2,RoundingMode.HALF_UP),
            message
        );
    }

    //1. RECORDING THE NEWLY AWARDED INTEREST AS A NEW PAYMENT/DEPOSIT
    private Payment applyInterestAsPayment(UUID planUuid, BigDecimal interestAmount,  PaymentType interestType){
    Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);

    if(retrievedSavingsPlan.isPresent() && interestAmount.compareTo(BigDecimal.ZERO) > 0){
        //We can't apply a non-positive (zero/negative) amount of interest
        SavingsPlan savingsPlan = retrievedSavingsPlan.get();

        Payment interestPayment = new Payment();

        interestPayment.setSavingsPlan(savingsPlan);
        interestPayment.setAmount(interestAmount);
        interestPayment.setPaymentDate(LocalDate.now());
        interestPayment.setPaymentType(interestType);
        interestPayment.setPaymentMethod("SYSTEM_AWARDED_INTEREST");
        interestPayment.setTransactionId("INTEREST-"+ interestType.name() + "-" + UUID.randomUUID().toString().substring(0,8));

        return paymentRepository.save(interestPayment);
    }
    else{
        throw new IllegalArgumentException("Cannot apply interest. Savings Plan not found/ Interest amount is zero");
    }
    }
    //Total deposited amount
    public BigDecimal calculateTotalDeposit(UUID planUuid){
            List<Payment> payments = paymentRepository.findBySavingsPlan_PlanUuidOrderByPaymentDateAsc(planUuid);
            return payments.stream()
                 .map(Payment::getAmount)
                 .reduce(BigDecimal.ZERO, BigDecimal::add);

        }
    public BigDecimal calculateRemainingAmount(UUID planUuid){
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);

        if(retrievedSavingsPlan.isPresent()){
            SavingsPlan savingsPlan = retrievedSavingsPlan.get();
            BigDecimal totalDeposits = calculateTotalDeposit(planUuid);
            BigDecimal remainingAmount = savingsPlan.getAmount().subtract(totalDeposits);
            //return savingsPlan.getAmount() - totalDeposits;
        
            if(remainingAmount.compareTo(BigDecimal.ZERO) < 0){
                return BigDecimal.ZERO;
            }
            else{
                return remainingAmount;
            }
        }
        throw new IllegalArgumentException("Savings Plan not found");        
    }
    public double calculatePercentageCompleted(UUID planUuid){
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);

        if(retrievedSavingsPlan.isPresent()){
            SavingsPlan savingsPlan = retrievedSavingsPlan.get();
            BigDecimal totalDeposits = calculateTotalDeposit(planUuid);
            BigDecimal targetAmount = savingsPlan.getAmount();

            if(targetAmount.compareTo(BigDecimal.ZERO) <= 0){
                return 0.0;
            }
            double percentage = totalDeposits.divide(targetAmount, 4, RoundingMode.HALF_UP).doubleValue() * 100;
            //Ensuring the percentage doesn't exceed 100% incase user overpays
            return Math.min(percentage, 100.0);
        }
        return 0.0;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////
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
                return BigDecimal.ZERO;
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
}
