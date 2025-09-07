package com.drivefundproject.drive_fund.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsDetailsResponse {
    private Integer id;
   // private String firstname;
    //private String imageUrl;
    private String productname;
    private Double amount;
    private String timeline;

    // public SavingsDetailsResponse(String productname, Double amount, String timeline) {
    //     this.productname = productname;
    //     this.amount = amount;
    //     this.timeline = timeline;
    // }

}
