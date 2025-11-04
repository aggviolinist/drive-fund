package com.drivefundproject.drive_fund.user.addsavingsplan.service;


import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.auth.model.User;
import com.drivefundproject.drive_fund.auth.repository.UserRepository;
import com.drivefundproject.drive_fund.user.addsavingsplan.dto.request.SavingsPlanRequest;
import com.drivefundproject.drive_fund.user.addsavingsplan.model.SavingsPlan;
import com.drivefundproject.drive_fund.user.addsavingsplan.model.Status;
import com.drivefundproject.drive_fund.user.addsavingsplan.repository.SavingsPlanRepository;
import com.drivefundproject.drive_fund.user.catalogue.model.Catalogue;
import com.drivefundproject.drive_fund.user.catalogue.repository.CatalogueRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SavingsPlanService  {

    private final SavingsPlanRepository savingsPlanRepository;
    private final CatalogueRepository catalogueRepository;
    private final UserRepository userRepository;

    public SavingsPlan addSavingsPlan(SavingsPlanRequest savingsPlanRequest, Integer userId){
        Catalogue catalogueItem = catalogueRepository.findByCatUuid(savingsPlanRequest.getCatUuid())
            .orElseThrow(() -> new RuntimeException("Product not found"));


        User user = userRepository.findById(userId) 
            .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<SavingsPlan> existingPlan = savingsPlanRepository.findByUserIdAndCatalogueCatUuid(userId, savingsPlanRequest.getCatUuid());
        if(existingPlan.isPresent()){
            throw new RuntimeException("A savings plan for this product already exists for you");
        }

        LocalDate creationDate = LocalDate.now();

        LocalDate targetCompletionDate = creationDate.plusMonths(savingsPlanRequest.getTimeline());

        SavingsPlan savingsPlan = SavingsPlan.builder()
             .timeline(savingsPlanRequest.getTimeline())
             .amount(savingsPlanRequest.getAmount())
             .catalogue(catalogueItem)
             .user(user)
             .creationDate(creationDate)
             .targetCompletionDate(targetCompletionDate)
             .frequency(savingsPlanRequest.getFrequency())
             .status(Status.PENDING)
             .build();
        //save user in SavingsPlan table
        return savingsPlanRepository.save(savingsPlan);

    }  
}
