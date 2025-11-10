package com.drivefundproject.drive_fund.admin.customsystemvariables.dto.response;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllCustomVariablesResponse {
    private String interestName;
    private BigDecimal interestValue;
    private LocalDate createdAt;   
}
