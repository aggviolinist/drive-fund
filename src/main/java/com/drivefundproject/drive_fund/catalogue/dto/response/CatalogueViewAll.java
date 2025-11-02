package com.drivefundproject.drive_fund.catalogue.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogueViewAll {
    private UUID catUuid;
    private String productname; 
    private String productdesc;
    private String productImageUrl;

}
