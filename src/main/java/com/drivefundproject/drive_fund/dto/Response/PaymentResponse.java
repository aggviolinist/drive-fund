package com.drivefundproject.drive_fund.dto.Response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.drivefundproject.drive_fund.model.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentResponse {
    private UUID planUuid;
    private UUID paymentUuid;
    private BigDecimal paymentAmount;
    private LocalDate payment_date;
    private String payment_method;
    private String transaction_id;
    private Status status;





    
}
