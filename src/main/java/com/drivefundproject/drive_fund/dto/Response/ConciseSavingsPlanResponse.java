package com.drivefundproject.drive_fund.dto.Response;

import java.time.LocalDate;
import java.util.UUID;

import com.drivefundproject.drive_fund.model.Frequency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConciseSavingsPlanResponse {
    //private Integer id;
    private UUID plan_uuid;
    private Double amount;
    private Integer timeline;
    private LocalDate creationDate;
    private LocalDate targetCompletionDate;
    private Frequency frequency;
    //private Double expectedPayment;
    private CustomCataloguePlanResponse catalogueResponse;
    private CustomUserSavingsPlanResponse userSavingsPlanResponse;
}
