package com.drivefundproject.drive_fund.admin.customsystemvariables.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomSystemVariablesResponse {

    private String interest_name;
    private BigDecimal interest_value;
    private String interest_update_message;
}
