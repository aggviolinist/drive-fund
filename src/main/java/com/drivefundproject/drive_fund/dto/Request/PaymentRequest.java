package com.drivefundproject.drive_fund.dto.Request;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private BigDecimal amount; 
}
