package com.drivefundproject.drive_fund.dto.Response;

import java.math.BigDecimal;
import java.util.UUID;

public class InterestResponse {
        private UUID planUUid;
        private BigDecimal targetAmount;
        private BigDecimal paidTillToday;
        private double percentageCompleted;
        private String Message;
    
}
