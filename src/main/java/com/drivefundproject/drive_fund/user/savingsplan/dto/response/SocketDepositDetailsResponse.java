package com.drivefundproject.drive_fund.user.savingsplan.dto.response;

import lombok.Setter;

import com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.dto.response.SavingsProgressResponse;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.dto.response.InterestResponse;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.dto.response.PaymentResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Setter
@AllArgsConstructor
public class SocketDepositDetailsResponse {

    private PaymentResponse paymentResponse;
    private SavingsProgressResponse savingsProgressResponse;
    private InterestResponse interestResponse;
    private String Message;
}
