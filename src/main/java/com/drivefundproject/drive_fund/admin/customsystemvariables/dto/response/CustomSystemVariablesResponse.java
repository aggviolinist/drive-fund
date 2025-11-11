package com.drivefundproject.drive_fund.admin.customsystemvariables.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomSystemVariablesResponse {

    private String interestName;
    private BigDecimal interestValue;
    private LocalDate createdAt;   
}
