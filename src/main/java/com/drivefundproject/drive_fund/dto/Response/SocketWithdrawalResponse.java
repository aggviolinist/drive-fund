package com.drivefundproject.drive_fund.dto.Response;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.cglib.core.Local;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocketWithdrawalResponse {
    private SavingsProgressResponse savingsProgressResponse;
    private BigDecimal withdrawnAmount;
    private LocalDate withdrawalDate;
    private String Message;   
}
