package com.drivefundproject.drive_fund.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsDetailsRequest {
    private Integer id;
    private String fname;
    private String imageUrl;
    private String productname;
    private Double amount;
    private String timeline;
}
