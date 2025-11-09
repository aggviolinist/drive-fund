package com.drivefundproject.drive_fund.admin.fetchSavingsPlan.service;


import org.springframework.stereotype.Component;

import com.drivefundproject.drive_fund.user.addsavingsplan.dto.response.ConciseSavingsPlanResponse;
import com.drivefundproject.drive_fund.user.addsavingsplan.dto.response.CustomCataloguePlanResponse;
import com.drivefundproject.drive_fund.user.addsavingsplan.dto.response.CustomUserSavingsPlanResponse;
import com.drivefundproject.drive_fund.user.addsavingsplan.model.SavingsPlan;

@Component
public class FetchSavingsPlanMapper {

    public ConciseSavingsPlanResponse toConciseSavingsPlanResponse(SavingsPlan savingsPlan) {
        
        // Map nested User details
        CustomUserSavingsPlanResponse userResponse = new CustomUserSavingsPlanResponse(
            savingsPlan.getUser().getId(),
            savingsPlan.getUser().getFirstname(),
            savingsPlan.getUser().getImageUrl()
        );

        // Map nested Catalogue details
        CustomCataloguePlanResponse catalogueResponse = new CustomCataloguePlanResponse(
            savingsPlan.getCatalogue().getCatUuid(),
            savingsPlan.getCatalogue().getProductname(),
            savingsPlan.getCatalogue().getProductdesc()
        );

        // Map main SavingsPlan details
        return new ConciseSavingsPlanResponse(
            savingsPlan.getPlanUuid(),
            savingsPlan.getAmount(),
            savingsPlan.getTimeline(),
            savingsPlan.getCreationDate(),
            savingsPlan.getTargetCompletionDate(),
            savingsPlan.getFrequency(),
            savingsPlan.getStatus(),
            catalogueResponse,
            userResponse
        );
    }
}