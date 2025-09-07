package com.drivefundproject.drive_fund.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomCataloguePlanResponse {
    private Integer id;
    private String productname;
    private String productdesc;    
}
