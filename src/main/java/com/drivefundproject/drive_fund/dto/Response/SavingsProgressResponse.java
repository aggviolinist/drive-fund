package com.drivefundproject.drive_fund.dto.Response;

import java.math.BigDecimal;
import java.util.UUID;

import com.drivefundproject.drive_fund.model.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SavingsProgressResponse {
    private UUID planUUid;
    private String productName;
    private BigDecimal targetAmount;
    private BigDecimal paidTillToday;
    private BigDecimal remainingAmount;
    private BigDecimal TotalExpectedSavingsTillTodayAsPerYourSavingsFrequency;
    //private BigDecimal paidTillnow;
    private BigDecimal ArrearsTillToday;
    private BigDecimal newExpectedPayment;
    private double percentageCompleted;
    private Status status;
    private String timeRemaining;
    private String note;
    
}
