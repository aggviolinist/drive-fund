package com.drivefundproject.drive_fund.dto.Response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentResponse {
    private UUID planUuid;
    private UUID payment_uuid;
    private BigDecimal amount;
    private LocalDate payment_date;
    private String payment_method;
    private String transaction_id;





    
}
