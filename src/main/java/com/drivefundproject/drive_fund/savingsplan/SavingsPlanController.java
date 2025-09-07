package com.drivefundproject.drive_fund.savingsplan;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drivefundproject.drive_fund.dto.Request.SavingsPlanRequest;
import com.drivefundproject.drive_fund.dto.Response.ResponseHandler;
import com.drivefundproject.drive_fund.dto.Response.CustomCataloguePlanResponse;
import com.drivefundproject.drive_fund.dto.Response.CustomSavingsPlanResponse;
import com.drivefundproject.drive_fund.dto.Response.CustomUserSavingsPlanResponse;
import com.drivefundproject.drive_fund.model.SavingsPlan;
import com.drivefundproject.drive_fund.model.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/savings")
public class SavingsPlanController {

    private final SavingsPlanService savingsPlanService;

    @PostMapping("/add-plan")
    public ResponseEntity<Object> addSavingsPlan(@Valid @RequestBody SavingsPlanRequest savingsPlanRequest, @AuthenticationPrincipal User user){
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      //  User currentUser = (User) authentication.getPrincipal();
        Integer userId = user.getId();
        
        SavingsPlan newSavingsPlan = savingsPlanService.addSavingsPlan(savingsPlanRequest, userId);

        CustomUserSavingsPlanResponse userSavingsPlanResponse = new CustomUserSavingsPlanResponse(
          user.getId(),
          user.getFirstname(),
          user.getImageUrl()
          );

        CustomCataloguePlanResponse catalogueResponse = new CustomCataloguePlanResponse(
          newSavingsPlan.getCatalogue().getId(),
          newSavingsPlan.getCatalogue().getProductname(),
          newSavingsPlan.getCatalogue().getProductdesc()
        );
        CustomSavingsPlanResponse savingsPlanResponseDTO = new CustomSavingsPlanResponse(
          newSavingsPlan.getId(),
          newSavingsPlan.getAmount(),
          newSavingsPlan.getTimeline(),
          catalogueResponse,
          userSavingsPlanResponse
        );
        

        return ResponseHandler.generateResponse(HttpStatus.CREATED, "Savings plan created successfully", savingsPlanResponseDTO);

    }
    
}
