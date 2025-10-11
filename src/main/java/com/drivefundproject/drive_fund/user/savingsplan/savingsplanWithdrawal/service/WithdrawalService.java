package com.drivefundproject.drive_fund.savingsplan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.dto.Response.InterestResponse;
import com.drivefundproject.drive_fund.model.Payment;
import com.drivefundproject.drive_fund.model.WithdrawalType;
import com.drivefundproject.drive_fund.model.Withdrawals;
import com.drivefundproject.drive_fund.model.SavingsPlan;
import com.drivefundproject.drive_fund.model.Status;
import com.drivefundproject.drive_fund.model.WithdrawalFee;
import com.drivefundproject.drive_fund.repository.PaymentRepository;
import com.drivefundproject.drive_fund.repository.SavingsPlanRepository;
import com.drivefundproject.drive_fund.repository.WithdrawalsRepository;
import com.drivefundproject.drive_fund.repository.WithdrawalFeeRepository;



import lombok.RequiredArgsConstructor;

import com.drivefundproject.drive_fund.repository.SavingsPlanRepository;


@Service
@RequiredArgsConstructor
public class WithdrawalService {

    private final SavingsPlanRepository savingsPlanRepository;
    private final WithdrawalsRepository withdrawalRepository;
    private final WithdrawalFeeRepository withdrawalFeeRepository;

    private final BigDecimal WITHDRAWALPENALTY_FEE_RATE = new BigDecimal("0.10"); //10% penalty on early withdrawal
    private final BigDecimal WITHDRAWAL_FEE_RATE = new BigDecimal("0.02"); //2% Transcation fee on end of tenure
    private final PaymentService paymentService;


    public void recordWithdrawal(UUID planUuid, BigDecimal withdrawnAmount){
        //Validate Savings Plan and Status
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);
        if(withdrawnAmount == null)
        {
            throw new IllegalArgumentException("Withdrawal Amount must be provided.");

        }
        
        if(!retrievedSavingsPlan.isPresent()){
            throw new IllegalArgumentException("Savings Plan not found.");
        }
        //1. Determine the applicable fee rate and type based on plan status
        SavingsPlan savingsPlan = retrievedSavingsPlan.get();

        BigDecimal feeRate;
        WithdrawalType feeType;
        String feeReason;

        if(savingsPlan.getStatus() == Status.PENDING || savingsPlan.getStatus() == Status.IN_PROGRESS){
            feeRate = WITHDRAWALPENALTY_FEE_RATE;
            feeType = WithdrawalType.WITHDRAWAL_PENALTY;
            feeReason = "Early Withdrawal Penalty (10%)";
        }
        else{
            //Apply 2% transaction fee for completed plans/normal withdrawal 
            feeRate = WITHDRAWAL_FEE_RATE;
            feeType = WithdrawalType.WITHDRAWAL_FEE;
            feeReason = "Normal Withdrawal Transaction Fee (2%)";
            
        }
        //2. Calculate Costs and Validate Balance
        BigDecimal feeAmount = withdrawnAmount.multiply(feeRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalWithdrawalCost = withdrawnAmount.add(feeAmount);
        BigDecimal TotalDeposits = paymentService.calculateTotalDeposit(planUuid);
        BigDecimal maxAllowableWithdrawal = TotalDeposits.divide(BigDecimal.ONE.add(feeRate), 2, RoundingMode.HALF_UP);

        if(TotalDeposits.compareTo(totalWithdrawalCost) < 0){
            throw new IllegalArgumentException( "Withdrawal request exceeds available balance. You can withdraw up to " + maxAllowableWithdrawal
        + " considering the " + feeReason + " of " + feeRate.multiply(new BigDecimal("100")) + "%.");
        }

        //3. Record the Withdrawal (Negative Payment)
        Withdrawals withdrawals = new Withdrawals();
        withdrawals.setSavingsPlan(savingsPlan);
        withdrawals.setWithdrawalAmount(withdrawnAmount);
        withdrawals.setWithdrawalType(feeType);
        withdrawals.setDateWithdrawn(LocalDate.now());
        withdrawals.setSystemMessage(feeReason);
        withdrawals.setTransactionId("WITHDRAW-" + UUID.randomUUID().toString().substring(0,8));
        withdrawalRepository.save(withdrawals);

        //4. Record penalty(Positive payment to the platform)
        WithdrawalFee withdrawalFee = new WithdrawalFee();
        withdrawalFee.setSavingsPlan(savingsPlan);
        withdrawalFee.setFeeAmount(feeAmount);
        withdrawalFee.setWithdrawalType(feeType);
        withdrawalFee.setDateWithdrawalFeeEarned(LocalDate.now());
        withdrawalFee.setSystemMessage(feeReason);
        withdrawalFee.setTransactionId(feeType.name() + "-" + UUID.randomUUID().toString().substring(0,8));
        withdrawalFeeRepository.save(withdrawalFee);

        System.out.println("Successful withdrawal of:" + withdrawnAmount.setScale(2,RoundingMode.HALF_UP) + ". " + feeReason + " of " + feeAmount.setScale(2, RoundingMode.HALF_UP) + " applied.");
    }
    
}
