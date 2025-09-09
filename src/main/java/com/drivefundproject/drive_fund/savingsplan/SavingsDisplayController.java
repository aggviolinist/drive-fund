package com.drivefundproject.drive_fund.savingsplan;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drivefundproject.drive_fund.dto.Response.ConciseSavingsDisplayResponse;
import com.drivefundproject.drive_fund.dto.Response.ResponseHandler;
import com.drivefundproject.drive_fund.dto.Response.CustomSavingsDisplayResponse;
import com.drivefundproject.drive_fund.model.SavingsPlan;
import com.drivefundproject.drive_fund.model.User;
import com.drivefundproject.drive_fund.repository.SavingsPlanRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/savings")
public class SavingsDisplayController {

    private final SavingsPlanRepository savingsPlanRepository;
    private final SavingsDisplayService savingsDisplayService;

    // SavingsDetailsController(SavingsPlanRepository savingsPlanRepository) {
    //     this.savingsPlanRepository = savingsPlanRepository;
    // }

    @GetMapping("/details")
    public ResponseEntity<Object> getSavingsPlanForUser(@AuthenticationPrincipal User user) {
        if(user == null){
            return ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED, "User Not authenticated", null);
        }
        List<SavingsPlan> savingsPlan = savingsDisplayService.getSavingsPlanByUserId(user.getId());

        if(savingsPlan.isEmpty()){
            return ResponseHandler.generateResponse(HttpStatus.NOT_FOUND, "Please add a savings plan",null);            
        }

        ConciseSavingsDisplayResponse conciseSavingsDisplayResponse  = new ConciseSavingsDisplayResponse(user, savingsPlan);

        return ResponseHandler.generateResponse(HttpStatus.OK,"Saving Plan fetched successfully",conciseSavingsDisplayResponse);
    }

    @GetMapping("/checkout/{planId}")
    public ResponseEntity<Object> getSavingsPlanCheckout(@AuthenticationPrincipal User user, @PathVariable Integer planId){
        if(user == null){
            return ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED,"User not authenticated", null);
        }
        Optional<SavingsPlan> optionalSavingsPlan = savingsPlanRepository.findById(planId);

        if(!optionalSavingsPlan.isPresent()){
            return ResponseHandler.generateResponse(HttpStatus.NOT_FOUND,"Savings plan not found", null);    
        }
        SavingsPlan savingsPlan = optionalSavingsPlan.get();

        //Ensuring the user is the one who own the plan
        if(!savingsPlan.getUser().getId().equals(user.getId())){
            return ResponseHandler.generateResponse(HttpStatus.FORBIDDEN,"Access Denied", null);
        }
    }
    
    
}