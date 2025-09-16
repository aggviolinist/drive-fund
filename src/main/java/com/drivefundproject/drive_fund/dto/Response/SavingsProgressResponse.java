package com.drivefundproject.drive_fund.dto.Response;

import java.math.BigDecimal;
import java.util.UUID;

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
    private BigDecimal totalDeposited;
    private BigDecimal remainingAmount;
    private BigDecimal newExpectedPayment;
    private double percentageCompleted;
    
}
