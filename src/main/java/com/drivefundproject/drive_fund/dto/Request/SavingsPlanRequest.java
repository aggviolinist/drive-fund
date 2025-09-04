package com.drivefundproject.drive_fund.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingsPlanRequest {
    private Integer productID;
    private String timeline;
    private Double amount;
    
}
