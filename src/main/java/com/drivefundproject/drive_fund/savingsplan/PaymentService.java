package com.drivefundproject.drive_fund.savingsplan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.model.Frequency;
import com.drivefundproject.drive_fund.model.Payment;
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

    public Payment recordPaymentDeposit( UUID planUuid, BigDecimal paymentAmount){
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);
        //We want to avoid overpayment 
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
            //Ensuring the percentage doesn't exceed 100% incasea user overpays
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
