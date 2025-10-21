package com.drivefundproject.drive_fund.admin.customsystemvariables.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomSystemVariablesRequest {

    @NotBlank(message = "Interest name is required")
    private String interestName;
    
    @NotNull(message = "Interest value is required")
    private String interestValue;
    
}
