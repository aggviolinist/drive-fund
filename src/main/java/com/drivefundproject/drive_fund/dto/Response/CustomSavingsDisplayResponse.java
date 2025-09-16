package com.drivefundproject.drive_fund.dto.Response;

import java.time.LocalDate;
import java.util.UUID;

import com.drivefundproject.drive_fund.model.Frequency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomSavingsDisplayResponse {
    //private Integer id;
    private UUID planUuid;
    //private String firstname;
    //private String imageUrl;
    private String productname;
    private BigDecimal amount;
    private Integer timeline;
    private LocalDate creationDate;
    private LocalDate targetCompletionDate;
    private Frequency frequency;
    //private Double expectedPayment;

    // public SavingsDetailsResponse(String productname, Double amount, String timeline) {
    //     this.productname = productname;
    //     this.amount = amount;
    //     this.timeline = timeline;
    // }

}
