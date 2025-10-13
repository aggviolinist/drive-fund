package com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.drivefundproject.drive_fund.user.addsavingsplan.model.Frequency;
import com.drivefundproject.drive_fund.user.addsavingsplan.model.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomSavingsDisplayResponse {
    private UUID planUuid;
    private String productname;
    private BigDecimal amount;
    private Integer timeline;
    private LocalDate creationDate;
    private LocalDate targetCompletionDate;
    private Frequency frequency;
    private Status status;

}
