package com.drivefundproject.drive_fund.savingsplan;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.drivefundproject.drive_fund.dto.Response.ConciseSavingsPlanResponse;
import com.drivefundproject.drive_fund.dto.Response.CustomCataloguePlanResponse;
import com.drivefundproject.drive_fund.dto.Response.CustomUserSavingsPlanResponse;
import com.drivefundproject.drive_fund.model.SavingsPlan;
import com.drivefundproject.drive_fund.model.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/savings")
@PreAuthorize("hasRole('USER')") 
public class SavingsPlanController {

    private final SavingsPlanService savingsPlanService;

    @PostMapping("/add-plan")
    public ResponseEntity<Object> addSavingsPlan(@Valid @RequestBody SavingsPlanRequest savingsPlanRequest, @AuthenticationPrincipal User user){
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      //  User currentUser = (User) authentication.getPrincipal();
      try{
        Integer userId = user.getId();
        
        SavingsPlan newSavingsPlan = savingsPlanService.addSavingsPlan(savingsPlanRequest, userId);

        CustomUserSavingsPlanResponse userSavingsPlanResponse = new CustomUserSavingsPlanResponse(
          user.getId(),
          user.getFirstname(),
          user.getImageUrl()
          );

        CustomCataloguePlanResponse catalogueResponse = new CustomCataloguePlanResponse(
          //newSavingsPlan.getCatalogue().getId(),
          newSavingsPlan.getCatalogue().getCatUuid(),
          newSavingsPlan.getCatalogue().getProductname(),
          newSavingsPlan.getCatalogue().getProductdesc()
        );
        ConciseSavingsPlanResponse savingsPlanResponseDTO = new ConciseSavingsPlanResponse(
          //newSavingsPlan.getId(),
          newSavingsPlan.getPlanUuid(),
          newSavingsPlan.getAmount(),
          newSavingsPlan.getTimeline(),
          newSavingsPlan.getCreationDate(),
          newSavingsPlan.getTargetCompletionDate(),
          newSavingsPlan.getFrequency(),
          newSavingsPlan.getStatus(),
          catalogueResponse,
          userSavingsPlanResponse
        );
        return ResponseHandler.generateResponse(HttpStatus.CREATED, "Savings plan created successfully", savingsPlanResponseDTO);
      } catch (RuntimeException e){
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, "This savings plan already exists, Create a new plan", null);
      }
    }   
}