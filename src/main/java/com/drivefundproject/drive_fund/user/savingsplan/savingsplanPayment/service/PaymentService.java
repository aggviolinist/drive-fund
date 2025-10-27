package com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import com.drivefundproject.drive_fund.user.savingsplan.service.SavingsCalculationService;
import com.drivefundproject.drive_fund.user.savingsplan.service.SavingsPlanWebSocketAsyncService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class PaymentService {
    private final SavingsPlanRepository savingsPlanRepository;
    private final PaymentRepository paymentRepository;
    private final InterestEarnedService interestEarnedService;
    private final WithdrawalFeeRepository withdrawalFeeRepository;
    private final WithdrawalsRepository withdrawalsRepository;
    private final SavingsPlanWebSocketAsyncService savingsPlanWebSocketAsyncService;
    private final SavingsCalculationService savingsCalculationService;

    @CacheEvict(value = {
        "totalDeposit",
        "remainingAmount",
        "percentageCompleted",
        "dynamicExpectedPayment"
    }, key = "#planUuid")
    public Payment recordPaymentDeposit( UUID planUuid, BigDecimal paymentAmount){
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);
        //We want to avoid overpayment of the target amount
        if(retrievedSavingsPlan.isPresent()){
            SavingsPlan savingsPlan = retrievedSavingsPlan.get();

            BigDecimal netAmountPaidSoFar = savingsCalculationService.calculateTotalDeposit(planUuid);
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

            //COMPLETE status logic
            BigDecimal updatedTotalDeposits = savingsCalculationService.calculateTotalDeposit(planUuid);
            if(updatedTotalDeposits.compareTo(targetAmount) >=0 ){
                savingsPlan.setStatus(Status.COMPLETED);
                savingsPlanRepository.save(savingsPlan);
            }
            //Trigger ASYNCHRONOUS UPDATE
            savingsPlanWebSocketAsyncService.handleDepositAndPushUpdates(planUuid, savedPayment, paymentAmount);
            return savedPayment;
        }
        else{
            throw new IllegalArgumentException("Savings Plan not found");
        }
    }
    //CALCULATING INTEREST 50% AND 75% INTEREST
    public InterestResponse calculateInterest(UUID planUuid, double percentageCompleted){
             return interestEarnedService
                     .calculateAndApplyInterest(planUuid, percentageCompleted);
    }      
}
