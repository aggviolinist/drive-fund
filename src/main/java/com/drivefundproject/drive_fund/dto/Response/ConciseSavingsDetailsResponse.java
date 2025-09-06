package com.drivefundproject.drive_fund.dto.Response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConciseSavingsDetailsResponse {
    private String firstname;
    private String imageUrl;
    private List<SavingsDetailsResponse> savingsDetailsResponse;

    public ConciseSavingsDetailsResponse(User user, List<SavingsPlan> savingsPlan ){

    }
    
}
