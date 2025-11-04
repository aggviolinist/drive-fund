package com.drivefundproject.drive_fund.user.catalogue.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CatalogueResponse {
    private String productImageUrl;
    private UUID catUuid;
    private String productname; 
    private String productdesc;   
}
