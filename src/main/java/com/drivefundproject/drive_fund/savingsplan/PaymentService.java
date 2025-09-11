package com.drivefundproject.drive_fund.savingsplan;

import java.time.LocalDate;
import java.util.List;
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

    public void recordPaymentDeposit( UUID planUuid, Double paymentAmount){
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
    public Double calculateTotalDeposit(UUID planUuid){
            List<Payment> payments = PaymentRepository.findBySavingsPlan_PlanUuidOrderByPaymentDateAsc(planUuid);
            return payments.stream()
                 .mapToDouble(Payment::getAmount)
                 .sum();

        }
    public Double calculateRemainingAmount(UUID planUuid){
        
    }
    
}
