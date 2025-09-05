package com.drivefundproject.drive_fund.savingsplan;

import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.dto.Request.SavingsPlanRequest;
import com.drivefundproject.drive_fund.model.Catalogue;
import com.drivefundproject.drive_fund.model.SavingsPlan;
import com.drivefundproject.drive_fund.model.User;
import com.drivefundproject.drive_fund.repository.CatalogueRepository;
import com.drivefundproject.drive_fund.repository.SavingsPlanRepository;
import com.drivefundproject.drive_fund.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SavingsPlanService  {

    private final SavingsPlanRepository savingsPlanRepository;
    private final CatalogueRepository catalogueRepository;
    private final UserRepository userRepository;

    public SavingsPlan addSavingsPlan(SavingsPlanRequest savingsPlanRequest, Integer userId){
        Catalogue catalogueItem = catalogueRepository.findById(savingsPlanRequest.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));


        User user = userRepository.findById(userId) 
            .orElseThrow(() -> new RuntimeException("User not found"));

        SavingsPlan savingsPlan = SavingsPlan.builder()
             .timeline(savingsPlanRequest.getTimeline())
             .amount(savingsPlanRequest.getAmount())
             .catalogue(catalogueItem)
             .user(user)
             .build();
        //save user in SavingsPlan table
        return savingsPlanRepository.save(savingsPlan);

    }  
}
