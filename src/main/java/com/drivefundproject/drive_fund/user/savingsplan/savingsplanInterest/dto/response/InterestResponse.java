package com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InterestResponse {
        private UUID planUUid;
        private BigDecimal targetAmount;
        private BigDecimal paidTillToday;
        private double percentageCompleted;
        private BigDecimal interestAmount;
        private String Message;
    
}
