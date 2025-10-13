package com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.cglib.core.Local;

import com.drivefundproject.drive_fund.user.addsavingsplan.model.Frequency;
import com.drivefundproject.drive_fund.user.addsavingsplan.model.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsPlanCheckoutResponse {
   // private Integer product_id;
    private UUID planUuid; 
    private String product_name;
    private BigDecimal amount;
    private Integer timeline;
    private LocalDate creationDate;
    private LocalDate targetCompletionDate;
    private Frequency frequency;
    private Status status;
    private BigDecimal expectedPayment;
}
