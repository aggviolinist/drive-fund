package com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.user.addsavingsplan.model.Frequency;
import com.drivefundproject.drive_fund.user.addsavingsplan.model.SavingsPlan;
import com.drivefundproject.drive_fund.user.addsavingsplan.model.Status;
import com.drivefundproject.drive_fund.user.addsavingsplan.repository.SavingsPlanRepository;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.dto.response.InterestResponse;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.service.InterestEarnedService;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.model.Payment;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.model.TransactionType;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.repository.PaymentRepository;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawal.repository.WithdrawalsRepository;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawalFee.repository.WithdrawalFeeRepository;


import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class PaymentService {
    private final SavingsPlanRepository savingsPlanRepository;
    private final PaymentRepository paymentRepository;
    private final InterestEarnedService interestEarnedService;
    private final WithdrawalFeeRepository withdrawalFeeRepository;
    private final WithdrawalsRepository withdrawalsRepository;

    // @CacheEvict(value = {
    //     "totalDeposit",
    //     "remainingAmount",
    //     "percentageCompleted",
    //     "dynamicExpectedPayment"
    // }, key = "#planUuid")
    public Payment recordPaymentDeposit( UUID planUuid, BigDecimal paymentAmount){
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);
        //We want to avoid overpayment of the target amount
        if(retrievedSavingsPlan.isPresent()){
            SavingsPlan savingsPlan = retrievedSavingsPlan.get();

            BigDecimal netAmountPaidSoFar = calculateTotalDeposit(planUuid);
            BigDecimal newTotalDeposited = netAmountPaidSoFar.add(paymentAmount);
            BigDecimal targetAmount = savingsPlan.getAmount();

            if(newTotalDeposited.compareTo(targetAmount) > 0){
                throw new IllegalArgumentException("Payment amount exceeds the remaining savings goal. The remaining amount is:" + targetAmount.subtract(netAmountPaidSoFar));

            }
            //IN-PROGRESS Status Logic
            if(savingsPlan.getStatus() == Status.PENDING){
                savingsPlan.setStatus(Status.IN_PROGRESS);
                savingsPlanRepository.save(savingsPlan);
            }

            //Adding new payment
            Payment payment = new Payment();
            payment.setSavingsPlan(savingsPlan);
            payment.setPaymentAmount(paymentAmount);
            payment.setTransactionType(TransactionType.DEPOSIT); //Deposit interest as payment
            payment.setSystemMessage("USER_MADE_DEPOSIT");
            payment.setTransactionId("DEPOSIT-TXN-"+ UUID.randomUUID().toString().substring(0,8));
            payment.setPaymentDate(LocalDate.now());

            Payment savedPayment = paymentRepository.save(payment);

           // Change the status from COMPLETED to IN-PROGRESS after savings reach 100%
           if (netAmountPaidSoFar.compareTo(targetAmount) < 0) {
            // If the net amount is now LESS THAN the target amount, the status cannot be COMPLETED.
            if (savingsPlan.getStatus() == Status.COMPLETED || savingsPlan.getStatus() == Status.PENDING) {
                // Change status from COMPLETED to IN_PROGRESS
                savingsPlan.setStatus(Status.IN_PROGRESS); 
                savingsPlanRepository.save(savingsPlan);
            }
        }

           double percentageCompleted = calculatePercentageCompleted(planUuid);
           InterestResponse interestResponse = calculateInterest(planUuid, percentageCompleted);

            //COMPLETE status logic once the total deposits are more than the targetamount
            BigDecimal updatedTotalDeposits = calculateTotalDeposit(planUuid);
            if(updatedTotalDeposits.compareTo(targetAmount) >=0 ){
                savingsPlan.setStatus(Status.COMPLETED);
                savingsPlanRepository.save(savingsPlan);
            }
            //return new PaymentResponseWrapper(savedPayment, interestResponse);
            return savedPayment;
        }
        else{
            throw new IllegalArgumentException("Savings Plan not found");
        }
    }
    //CALCULATING INTEREST 50% AND 75% INTEREST
    public InterestResponse calculateInterest(UUID planUuid, double percentageCompleted){
             return interestEarnedService.calculateAndApplyInterest(planUuid, percentageCompleted);
    }
  
    //Total deposited amount
    // @Cacheable(value = "totalDeposit", key = "#planUuid")
    public BigDecimal calculateTotalDeposit(UUID planUuid){
        //1.Get total from user deposits(Payments table)
            // List<Payment> deposits = paymentRepository.findBySavingsPlan_PlanUuidOrderByPaymentDateAsc(planUuid);
            // BigDecimal totalDeposits = deposits.stream()
            //           .map(Payment::getPaymentAmount)
            //           .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalDeposits = paymentRepository.findTotalPaymentAmountBySavingsPlan_PlanUuid(planUuid);
        //2. Get total from earned interest (InterestEarned table)
           BigDecimal totalInterest = interestEarnedService.calculateTotalInterest(planUuid);
        //TOTAL WITHDRAWS FROM WITHDRAWALS TABLE
        //    List<Withdrawals> withdrawals = withdrawalsRepository.findBySavingsPlan_PlanUuid(planUuid);
        //    BigDecimal totalWithdrawn = withdrawals.stream()
        //               .map(Withdrawals::getWithdrawalAmount)
        //               .reduce(BigDecimal.ZERO,BigDecimal::add);
          BigDecimal totalWithdrawn = withdrawalsRepository.findTotalWithdrawalAmountBySavingsPlan_PlanUuid(planUuid);
        //TOTAL WITHDRAWAL FEE FROM THE WITHDRAWAL FEE TABLE
            // List<WithdrawalFee> withdrawalFees = withdrawalFeeRepository.findBySavingsPlan_PlanUuid(planUuid);
            // BigDecimal totalfee = withdrawalFees.stream()
            //           .map(WithdrawalFee::getFeeAmount)
            //           .reduce(BigDecimal.ZERO,BigDecimal::add);
            BigDecimal totalfee = withdrawalFeeRepository.findTotalFeeAmountBySavingsPlan_PlanUuid(planUuid);

            BigDecimal grossSavings = totalDeposits.add(totalInterest);
            BigDecimal totalDeductions = totalWithdrawn.add(totalfee);
            BigDecimal netAmountPaidSoFar =  grossSavings.subtract(totalDeductions);

            if (netAmountPaidSoFar.compareTo(BigDecimal.ZERO) < 0) {
                  netAmountPaidSoFar = BigDecimal.ZERO;
            }
                            
        //3. TotalDeposits - Totalinterest - Totalwithdrawals(fee/penalty)
        return netAmountPaidSoFar;
        }

    // @Cacheable(value = "remainingAmount", key = "#planUuid")
    public BigDecimal calculateRemainingAmount(UUID planUuid){
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);

        if(retrievedSavingsPlan.isPresent()){
            SavingsPlan savingsPlan = retrievedSavingsPlan.get();
            BigDecimal netAmountPaidSoFar = calculateTotalDeposit(planUuid);
            BigDecimal remainingAmount = savingsPlan.getAmount().subtract(netAmountPaidSoFar);
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
    
    // @Cacheable(value = "percentageCompleted", key = "#planUuid")
    public double calculatePercentageCompleted(UUID planUuid){
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);

        if(retrievedSavingsPlan.isPresent()){
            SavingsPlan savingsPlan = retrievedSavingsPlan.get();
            BigDecimal netAmountPaidSoFar = calculateTotalDeposit(planUuid);
            BigDecimal targetAmount = savingsPlan.getAmount();

            if(targetAmount.compareTo(BigDecimal.ZERO) <= 0){
                return 0.0;
            }
            double percentage = netAmountPaidSoFar.divide(targetAmount, 4, RoundingMode.HALF_UP).doubleValue() * 100;
            //Ensuring the percentage doesn't exceed 100% incase user overpays
            return Math.max(0.0,Math.min(percentage, 100.0));
        }
        return 0.0;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////

    // @Cacheable(value = "initialExpectedPayment", key = "#savingsPlan.planUuid")
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
    
    // @Cacheable(value = "dynamicExpectedPayment", key = "#planUuid")
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

