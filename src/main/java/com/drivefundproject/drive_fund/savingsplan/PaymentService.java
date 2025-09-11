package com.drivefundproject.drive_fund.savingsplan;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.model.Payment;
import com.drivefundproject.drive_fund.model.SavingsPlan;
import com.drivefundproject.drive_fund.repository.PaymentRepository;
import com.drivefundproject.drive_fund.repository.SavingsPlanRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class PaymentService {
    private final SavingsPlanRepository savingsPlanRepository;
    private final PaymentRepository paymentRepository;

    public void recordPayment( UUID planUuid, Double paymentAmount){
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);

        if(retrievedSavingsPlan.isPresent()){
            SavingsPlan savingsPlan = retrievedSavingsPlan.get();

            Payment payment = new Payment();
            payment.setSavingsPlan(savingsPlan);
            payment.setAmount(paymentAmount);
            payment.setPaymentDate(LocalDate.now());

            paymentRepository.save(payment);
        }
        else{
            throw new IllegalArgumentException("Savings Plan not found");
        }
    }
    
}
