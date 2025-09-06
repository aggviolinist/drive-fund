package com.drivefundproject.drive_fund.savingsplan;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drivefundproject.drive_fund.dto.Response.ResponseHandler;
import com.drivefundproject.drive_fund.dto.Response.SavingsDetailsResponse;
import com.drivefundproject.drive_fund.model.SavingsPlan;
import com.drivefundproject.drive_fund.model.User;
import com.drivefundproject.drive_fund.repository.SavingsPlanRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/savings")
public class SavingsDetailsController {

    private final SavingsPlanRepository savingsPlanRepository;
    private final SavingsDisplayService savingsDisplayService;

    // SavingsDetailsController(SavingsPlanRepository savingsPlanRepository) {
    //     this.savingsPlanRepository = savingsPlanRepository;
    // }

    @GetMapping("/details/user")
    public ResponseEntity<Object> getSavingsPlanForUser(@AuthenticationPrincipal User user) {
        if(user == null){
            return ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED, "User Not authenticated", null);
        }
        List<SavingsPlan> savingsPlan = savingsDisplayService.getSavingsPlanByUserId(user.getId());

        if(savingsPlan.isEmpty()){
            return ResponseHandler.generateResponse(HttpStatus.NOT_FOUND, "Please add a savings plan",null);            
        }

        List<SavingsDetailsResponse> savingsDetailsResponse = savingsPlan.stream()
        .map(plan -> new SavingsDetailsResponse(
            plan.getUser().getFirstname(),
            plan.getUser().getImageUrl(),
            plan.getCatalogue().getProductname(),
            plan.getAmount(),
            plan.getTimeline()
        ))
        .collect(Collectors.toList());

        return ResponseHandler.generateResponse(HttpStatus.OK,"Saving Plan fetched successfully",savingsDetailsResponse);
    }
    
    
}