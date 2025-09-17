package com.drivefundproject.drive_fund.savingsplan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

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

    public void recordPaymentDeposit( UUID planUuid, BigDecimal paymentAmount){
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

            //Adding new paymen
            Payment payment = new Payment();
            payment.setSavingsPlan(savingsPlan);
            payment.setAmount(paymentAmount);
            payment.setPaymentDate(LocalDate.now());

            paymentRepository.save(payment);

            //COMPLETE status logic
            BigDecimal updatedTotalDeposits = calculateTotalDeposit(planUuid);
            if(updatedTotalDeposits.compareTo(targetAmount) >=0 ){
                savingsPlan.setStatus(Status.COMPLETED);
                savingsPlanRepository.save(savingsPlan);
            }

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
}
