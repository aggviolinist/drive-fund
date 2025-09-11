package com.drivefundproject.drive_fund.dto.Response;

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
