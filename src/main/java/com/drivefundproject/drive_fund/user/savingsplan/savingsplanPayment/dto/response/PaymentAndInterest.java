package com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.dto.response;

import com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.dto.response.InterestResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAndInterest {
    private PaymentResponse paymentResponse;
    private InterestResponse interestResponse;
}
