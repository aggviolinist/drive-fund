package com.drivefundproject.drive_fund.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogueRequest {
    private String productname;
    private String productdesc;
}
