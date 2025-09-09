package com.drivefundproject.drive_fund.dto.Response;

import java.time.LocalDate;

import org.springframework.cglib.core.Local;

import com.drivefundproject.drive_fund.model.Frequency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsPlanCheckoutResponse {
    private Integer product_id;
    private String product_name;
    private Double amount;
    private Integer timeline;
    private LocalDate creationDate;
    private LocalDate targetCompletionDate;
    private Frequency frequency;
    private Double expectedPayment;
}
