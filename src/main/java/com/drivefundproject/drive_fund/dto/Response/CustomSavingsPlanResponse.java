package com.drivefundproject.drive_fund.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomSavingsPlanResponse {
    private Integer id;
    private Double amount;
    private String timeline;
    private CustomCataloguePlanResponse catalogueResponse;
    private CustomUserSavingsPlanResponse userSavingsPlanResponse;
}
