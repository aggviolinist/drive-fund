package com.drivefundproject.drive_fund.dto.Response;

import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Setter
@AllArgsConstructor
public class SocketDepositDetailsResponse {

    private PaymentResponse paymentResponse;
    private SavingsProgressResponse savingsProgressResponse;
    private String Message;
}
