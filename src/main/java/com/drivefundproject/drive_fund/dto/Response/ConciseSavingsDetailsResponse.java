package com.drivefundproject.drive_fund.dto.Response;

import java.util.List;
import java.util.stream.Collectors;

import com.drivefundproject.drive_fund.model.SavingsPlan;
import com.drivefundproject.drive_fund.model.User;

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
        this.firstname = user.getFirstname();
        this.imageUrl = user.getImageUrl();
        this.savingsDetailsResponse = savingsPlan.stream()
          .map(plan -> new SavingsDetailsResponse(
            //null, //firstname is not needed in this inner DTO
            //null,
            plan.getId(),
            plan.getCatalogue().getProductname(),
            plan.getAmount(),
            plan.getTimeline(),
            plan.getCreationDate(),
            plan.getTargetCompletionDate(),
            plan.getFrequency()
          ))
          .collect(Collectors.toList());

    }
    
}
