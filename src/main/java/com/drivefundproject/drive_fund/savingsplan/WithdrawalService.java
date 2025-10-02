package com.drivefundproject.drive_fund.savingsplan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.dto.Response.InterestResponse;
import com.drivefundproject.drive_fund.model.Payment;
import com.drivefundproject.drive_fund.model.PaymentType;
import com.drivefundproject.drive_fund.model.SavingsPlan;
import com.drivefundproject.drive_fund.model.Status;
import com.drivefundproject.drive_fund.model.WithdrawalType;
import com.drivefundproject.drive_fund.repository.SavingsPlanRepository;

import lombok.RequiredArgsConstructor;

import com.drivefundproject.drive_fund.repository.SavingsPlanRepository;


@Service
@RequiredArgsConstructor
public class WithdrawalService {

    private final SavingsPlanRepository savingsPlanRepository;

    private final BigDecimal WITHDRAWALPENALTY_FEE_RATE = new BigDecimal("0.10"); //10% penalty on early withdrawal
    private final BigDecimal WITHDRAWAL_FEE_RATE = new BigDecimal("0.02"); //2% Transcation fee on end of tenure
    private final PaymentService paymentService;


    public void recordWithdrawal(UUID planUuid, BigDecimal withdrawnAmount){
        //Validate Savings Plan and Status
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);

        if(!retrievedSavingsPlan.isPresent()){
            throw new IllegalArgumentException("Savings Plan not found.");
        }
        //1. Determine the applicable fee rate and type based on plan status
        SavingsPlan savingsPlan = retrievedSavingsPlan.get();

        BigDecimal feeRate;
        WithdrawalType feeType;
        String feeReason;

        if(savingsPlan.getStatus() != Status.COMPLETED){
            //Apply 2% transaction fee for completed plans/normal withdrawal
            feeRate = WITHDRAWAL_FEE_RATE;
            feeType = WithdrawalType.WITHDRAWAL_FEE;
            feeReason = "Normal Withdrawal Transaction Fee (2%)";
        }
        else{
            feeRate = WITHDRAWALPENALTY_FEE_RATE;
            feeType = WithdrawalType.WITHDRAWAL_PENALTY;
            feeReason = "Early Withdrawal Penalty (10%)";
        }
        //2. Calculate Costs and Validate Balance
        BigDecimal feeAmount = withdrawnAmount.multiply(feeRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalWithdrawalCost = withdrawnAmount.add(feeAmount);
        BigDecimal netBalance = paymentService.calculateTotalDeposit(planUuid);

        if(netBalance.compareTo(totalWithdrawalCost) < 0){
            throw new IllegalArgumentException("Withdrawl request of " + withdrawnAmount.setScale(2, RoundingMode.HALF_UP) + "plus" + feeReason + "of" + feeAmount.setScale(2, RoundingMode.HALF_UP) + "exceeds the available net balance of " + netBalance.setScale(2, RoundingMode.HALF_UP));
        }

        //3. Record the Withdrawal (Negative Payment)
        Payment withdrawal = new Payment();
        


    }
    
}
