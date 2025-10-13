package com.drivefundproject.drive_fund.user.addsavingsplan.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomCataloguePlanResponse {
   // private Integer id;
    private UUID catUuid;
    private String productname;
    private String productdesc;    
}
