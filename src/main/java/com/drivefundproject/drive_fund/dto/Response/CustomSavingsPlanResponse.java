package com.drivefundproject.drive_fund.dto.Response;

import java.time.LocalDate;

import com.drivefundproject.drive_fund.model.Frequency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomSavingsPlanResponse {
    private Integer id;
    private Double amount;
    private Integer timeline;
    private LocalDate creationDate;
    private LocalDate targetCompletionDate;
    private Frequency frequency;
    //private Double expectedPayment;
    private CustomCataloguePlanResponse catalogueResponse;
    private CustomUserSavingsPlanResponse userSavingsPlanResponse;
}
