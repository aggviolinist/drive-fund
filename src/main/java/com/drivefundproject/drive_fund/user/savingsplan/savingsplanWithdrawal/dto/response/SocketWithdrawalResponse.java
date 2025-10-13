package com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawal.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.cglib.core.Local;

import com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.dto.response.SavingsProgressResponse;

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
