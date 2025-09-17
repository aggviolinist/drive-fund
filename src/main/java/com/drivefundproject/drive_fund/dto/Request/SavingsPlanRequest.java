package com.drivefundproject.drive_fund.dto.Request;

import java.math.BigDecimal;
import java.util.UUID;

import com.drivefundproject.drive_fund.model.Frequency;

import jakarta.validation.constraints.DecimalMin;
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
    //private Integer productId;
    private UUID catUuid;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0") 
    private BigDecimal amount;

    @NotNull(message = "Timeline is required")  
    private Integer timeline;

    @NotNull(message = "Frequency is required")
    private Frequency frequency;
       
}
