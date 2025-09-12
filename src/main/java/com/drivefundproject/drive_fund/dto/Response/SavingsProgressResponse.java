package com.drivefundproject.drive_fund.dto.Response;

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
    private Double targetAmount;
    private Double totalDeposited;
    private Double remainingAmount;
    private Double newExpectedPayment;
    private double percentageCompleted;
    
}
