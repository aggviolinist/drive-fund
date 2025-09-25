package com.drivefundproject.drive_fund.savingsplan;

import java.lang.StackWalker.Option;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.dto.Response.SavingsProgressResponse;
import com.drivefundproject.drive_fund.model.Frequency;
import com.drivefundproject.drive_fund.model.Payment;
import com.drivefundproject.drive_fund.model.SavingsPlan;
import com.drivefundproject.drive_fund.repository.PaymentRepository;
import com.drivefundproject.drive_fund.repository.SavingsPlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavingsDisplayService {

    private final SavingsPlanRepository savingsPlanRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public List<SavingsPlan>getSavingsPlanByUserId(Integer userId){
        return savingsPlanRepository.findByUserId(userId);
    }
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
    public SavingsProgressResponse getSavingsProgress(UUID planUuid){
      //Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);
        Optional<SavingsPlan> retrievedSavingsPlan = savingsPlanRepository.findByPlanUuidWithCatalogueFetch(planUuid);

        if(retrievedSavingsPlan.isPresent()){
            SavingsPlan savingsPlan = retrievedSavingsPlan.get();

            BigDecimal targetAmount = savingsPlan.getAmount();
            BigDecimal totalDeposited = paymentService.calculateTotalDeposit(planUuid);
            BigDecimal balanceAmount = savingsPlan.getAmount().subtract(totalDeposited);
            double percentageCompleted = paymentService.calculatePercentageCompleted(planUuid);
            long roundedPecentage = (long) Math.min(percentageCompleted,100.0);

            BigDecimal newExpectedPayment = calculateDynamicExpectedPayment(planUuid,balanceAmount);
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
            BigDecimal initialAmountPerPeriod = calculateInitialExpectedPayment(savingsPlan);//targetAmount.divide(new BigDecimal(totalPeriods), 0, RoundingMode.HALF_UP);

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
    
}
