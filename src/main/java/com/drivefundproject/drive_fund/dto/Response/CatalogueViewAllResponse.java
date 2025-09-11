package com.drivefundproject.drive_fund.dto.Response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogueViewAllResponse {
    private UUID catUuid;
    private String productname; 
    private String productdesc;
}
