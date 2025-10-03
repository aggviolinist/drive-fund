package com.drivefundproject.drive_fund.dto.Response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalResponse {
    private SavingsProgressResponse savingsProgressResponse;
    private BigDecimal withdrawnAmount;
    private String Message;
    
}
