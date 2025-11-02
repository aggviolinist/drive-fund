package com.drivefundproject.drive_fund.catalogue.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogueRequest {

    @NotNull(message = "Product image is required")
    private MultipartFile productImage;

    @NotBlank(message = "Product name is required")
    private String productname;

    @NotBlank(message = "Product description is required")
    private String productdesc;
}
