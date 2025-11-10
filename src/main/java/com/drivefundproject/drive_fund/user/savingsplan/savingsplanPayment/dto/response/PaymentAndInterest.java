package com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.dto.response;

import com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.dto.response.InterestResponse;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.model.Payment;

@Data
@Builder

@AllArgsConstructor
public class PaymentAndInterest {
    private Payment payment;
    private InterestResponse interestResponse;
}
