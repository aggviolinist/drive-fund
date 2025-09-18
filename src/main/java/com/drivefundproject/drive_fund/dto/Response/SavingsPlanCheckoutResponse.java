package com.drivefundproject.drive_fund.dto.Response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.cglib.core.Local;

import com.drivefundproject.drive_fund.model.Frequency;
import com.drivefundproject.drive_fund.model.Status;

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
