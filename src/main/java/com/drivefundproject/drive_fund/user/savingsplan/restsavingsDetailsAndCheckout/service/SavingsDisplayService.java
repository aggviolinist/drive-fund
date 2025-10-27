package com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.service;


import java.lang.StackWalker.Option;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.user.addsavingsplan.model.Frequency;
import com.drivefundproject.drive_fund.user.addsavingsplan.model.SavingsPlan;
import com.drivefundproject.drive_fund.user.addsavingsplan.repository.SavingsPlanRepository;
import com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.dto.response.SavingsProgressResponse;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.dto.response.PaymentResponse;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.model.Payment;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.repository.PaymentRepository;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.service.PaymentService;
import com.drivefundproject.drive_fund.user.savingsplan.service.SavingsCalculationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavingsDisplayService {

    private final SavingsPlanRepository savingsPlanRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SavingsCalculationService savingsCalculationService;
    


    public List<SavingsPlan>getSavingsPlanByUserId(Integer userId){
        return savingsPlanRepository.findByUserId(userId);
    }
    public SavingsProgressResponse getSavingsProgress(UUID planUuid){
      //Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuidWithCatalogueFetch(planUuid);

        if(retrievedSavingsPlan.isPresent()){
            SavingsPlan savingsPlan = retrievedSavingsPlan.get();

            BigDecimal targetAmount = savingsPlan.getAmount();
            BigDecimal totalDeposited = savingsCalculationService.calculateTotalDeposit(planUuid);
            BigDecimal balanceAmount = savingsPlan.getAmount().subtract(totalDeposited);
            double percentageCompleted = savingsCalculationService.calculatePercentageCompleted(planUuid);
            long roundedPecentage = (long) Math.min(percentageCompleted,100.0);

            BigDecimal newExpectedPayment = savingsCalculationService.calculateDynamicExpectedPayment(planUuid,balanceAmount);
            String note;

            ChronoUnit unit;

            if(savingsPlan.getFrequency() == Frequency.DAILY){
                unit = ChronoUnit.DAYS;
            }
            else if(savingsPlan.getFrequency() == Frequency.WEEKLY){
                unit = ChronoUnit.WEEKS;
            }
            else if(savingsPlan.getFrequency() == Frequency.MONTHLY){
                unit = ChronoUnit.MONTHS;
            }
            else{
                throw new IllegalArgumentException("Unsupported frequency");
            }

            //Total and Elapsed periods
            long totalPeriods = savingsPlan.getCreationDate().until(savingsPlan.getTargetCompletionDate(), unit);
            long elapsedPeriods = savingsPlan.getCreationDate().until(LocalDate.now(), unit);

            if(totalPeriods <= 0){
                totalPeriods = 1;
            }

            //Inital expected contribution per period
            BigDecimal initialAmountPerPeriod = savingsCalculationService.calculateInitialExpectedPayment(savingsPlan);//targetAmount.divide(new BigDecimal(totalPeriods), 0, RoundingMode.HALF_UP);

            //Expected till now
            BigDecimal TotalExpectedSavingsTillTodayAsPerYourSavingsFrequency = initialAmountPerPeriod.multiply(new BigDecimal(elapsedPeriods));
            BigDecimal paidTillToday = totalDeposited;
            BigDecimal ArrearsTillToday = TotalExpectedSavingsTillTodayAsPerYourSavingsFrequency.subtract(paidTillToday);

            //Smooth adjustment for new expected
            long remainingPeriods = totalPeriods - elapsedPeriods;
            if(remainingPeriods <= 0){
                remainingPeriods = 1;
            }
            //Friendly note
            if(ArrearsTillToday.compareTo(BigDecimal.ZERO) > 0){
                note = "You are behind by" + ArrearsTillToday + ". The remaining amount has been redistributed as " +newExpectedPayment + "per" + savingsPlan.getFrequency().name().toLowerCase() + " to still meet your goal.";
            }
            else if(ArrearsTillToday.compareTo(BigDecimal.ZERO) < 0){
                note = "You are ahead by " + ArrearsTillToday.abs() + ". Your future payments are reduced to " + newExpectedPayment + "per" +savingsPlan.getFrequency().name().toLowerCase() + ".";
            }
            else{
                note = "You are on track. Your future expected payment is"+ newExpectedPayment + "per" + savingsPlan.getFrequency().name().toLowerCase() + ".";
            }

            //Countdown Calculation
            LocalDate currentDate = LocalDate.now();
            LocalDate targetDate = savingsPlan.getTargetCompletionDate();
            String countDown;

            if(targetDate.isAfter(currentDate)){
                long daysRemaining = ChronoUnit.DAYS.between(currentDate, targetDate);

                if(daysRemaining < 7){
                    countDown = daysRemaining + "day(s) remaining";
                }
                else{
                    long weeksRemaining = ChronoUnit.WEEKS.between(currentDate, targetDate);
                    countDown = weeksRemaining + " week(s) remaining";
                }
            }else{
                countDown = "You reached your goal!";
            }


            //this is the dynamic expected payment using remaining amount
            return new SavingsProgressResponse(
                savingsPlan.getPlanUuid(),
                savingsPlan.getCatalogue().getProductname(),
                targetAmount,
                paidTillToday,
                balanceAmount,
                TotalExpectedSavingsTillTodayAsPerYourSavingsFrequency,
                ArrearsTillToday,
                newExpectedPayment,
                roundedPecentage,
                savingsPlan.getStatus(),
                countDown,
                note
                );

        }
        throw new IllegalArgumentException("Savings Plan not found");

    }
    public PaymentResponse createPaymentResponse( Payment newPayment){
        if(newPayment == null){
            throw new IllegalArgumentException("Payment entity cannot be null!");
        }

        return new PaymentResponse(
               newPayment.getSavingsPlan().getPlanUuid(),
               newPayment.getPaymentUuid(),
               newPayment.getPaymentAmount(),
               newPayment.getPaymentDate(),
               //newPayment.getSystemMessage(),
               //newPayment.getTransactionId(),
               newPayment.getSavingsPlan().getStatus()
        );
    }
    
}
