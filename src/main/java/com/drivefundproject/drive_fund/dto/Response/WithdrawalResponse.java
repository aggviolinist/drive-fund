package com.drivefundproject.drive_fund.dto.Response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WithdrawalResponse {
    private UUID plaUuid;
    private BigDecimal targetAmount;
    private double percentageCompleted;
    private BigDecimal WithdrawnAmount;
    private String Message;
    
}
