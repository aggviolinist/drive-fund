package com.drivefundproject.drive_fund.user.addsavingsplan.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserSavingsPlanResponse {
    private Integer id;
    private String firstname;
    private String imageUrl;
}
