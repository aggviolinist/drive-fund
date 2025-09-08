package com.drivefundproject.drive_fund.dto.Request;

import com.drivefundproject.drive_fund.model.Frequency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingsPlanRequest {
    private Integer productId;

    @NotNull(message = "Amount is required")  
    private Double amount;

    @NotNull(message = "Timeline is required")  
    private Integer timeline;

    @NotNull(message = "Frequency is required")
    private Frequency frequency;
       
}
