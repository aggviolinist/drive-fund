package com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drivefundproject.drive_fund.auth.model.User;
import com.drivefundproject.drive_fund.exception.ResponseHandler;
import com.drivefundproject.drive_fund.user.addsavingsplan.model.SavingsPlan;
import com.drivefundproject.drive_fund.user.addsavingsplan.repository.SavingsPlanRepository;
import com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.dto.response.ConciseSavingsDisplayResponse;
import com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.dto.response.SavingsPlanCheckoutResponse;
import com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.dto.response.SavingsProgressResponse;
import com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.service.SavingsDisplayService;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.dto.request.RestPaymentRequest;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.dto.response.PaymentResponse;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.model.Payment;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/savings")
@PreAuthorize("hasRole('USER')") 
public class SavingsDisplayController {

    private final SavingsPlanRepository savingsPlanRepository;
    private final SavingsDisplayService savingsDisplayService;
    private final PaymentService paymentService;


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

    @GetMapping("/checkout/{planUuid}")
    public ResponseEntity<Object> getSavingsPlanCheckout(@AuthenticationPrincipal User user, @PathVariable UUID planUuid){
        if(user == null){
            return ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED,"User not authenticated", null);
        }
        Optional<SavingsPlan> optionalSavingsPlan = savingsPlanRepository.findByPlanUuid(planUuid);

        if(!optionalSavingsPlan.isPresent()){
            return ResponseHandler.generateResponse(HttpStatus.NOT_FOUND,"Savings plan not found", null);    
        }
        SavingsPlan savingsPlan = optionalSavingsPlan.get();

        //Ensuring the user is the one who own the plan
        if(!savingsPlan.getUser().getId().equals(user.getId())){
            return ResponseHandler.generateResponse(HttpStatus.FORBIDDEN,"Access Denied", null);
        }
        //Display the expected amount from the service method
        BigDecimal expectedPayment = paymentService.calculateInitialExpectedPayment(savingsPlan);

        //Map data from SavingsPlan and expected amount from the new DTO
        SavingsPlanCheckoutResponse checkoutResponse = new SavingsPlanCheckoutResponse(
            savingsPlan.getPlanUuid(),
            savingsPlan.getCatalogue().getProductname(),
            savingsPlan.getAmount(),
            savingsPlan.getTimeline(),
            savingsPlan.getCreationDate(),
            savingsPlan.getTargetCompletionDate(),
            savingsPlan.getFrequency(),
            savingsPlan.getStatus(),
            expectedPayment
        );
        return ResponseHandler.generateResponse(HttpStatus.OK, "Checkout details fetched successfully", checkoutResponse);
    }
    @PostMapping("/payment/{planUuid}")
        public ResponseEntity<Object> recordPayment(@AuthenticationPrincipal User user, @PathVariable UUID planUuid, @Valid @RequestBody RestPaymentRequest paymentRequest){
            if(user == null){
                return ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED,"User not authenticated" , null);
            }
            Optional<SavingsPlan> retrievedSavedPlan = savingsPlanRepository.findByPlanUuid(planUuid);
            if(!retrievedSavedPlan.isPresent() || !retrievedSavedPlan.get().getUser().getId().equals(user.getId())){
                return ResponseHandler.generateResponse(HttpStatus.FORBIDDEN,"Acess Denied or The Savings plan was not found" , null);
            }
            try{
              Payment newpaymentResponse = paymentService.recordPaymentDeposit(planUuid, paymentRequest.getAmount());
              PaymentResponse paymentResponse = new PaymentResponse(
                newpaymentResponse.getSavingsPlan().getPlanUuid(),
                newpaymentResponse.getPaymentUuid(),
                newpaymentResponse.getPaymentAmount(),
                newpaymentResponse.getPaymentDate(),
                //newpaymentResponse.getSystemMessage(),
                //newpaymentResponse.getTransactionId(),
                newpaymentResponse.getSavingsPlan().getStatus());

                return ResponseHandler.generateResponse(HttpStatus.OK, "Payment recorded successfully", paymentResponse);
            }
            catch(IllegalArgumentException e){
                return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST,e.getMessage(), null);

            }
            
        }
    @GetMapping("/progress/{planUuid}")
    public ResponseEntity<Object> getSavingsProgress(@AuthenticationPrincipal User user, @PathVariable UUID planUuid){
        if(user == null){
            return ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED, "User not authenticated", null);
        }

        Optional<SavingsPlan> retrievedSavedPlanzz = savingsPlanRepository.findByPlanUuid(planUuid);
        if(!retrievedSavedPlanzz.isPresent() || !retrievedSavedPlanzz.get().getUser().getId().equals(user.getId())){
            return ResponseHandler.generateResponse(HttpStatus.FORBIDDEN, "Access Denied or Savings Plan not found", null);
        }
        try{
        SavingsProgressResponse progressResponse = savingsDisplayService.getSavingsProgress(planUuid);
        return ResponseHandler.generateResponse(HttpStatus.OK,"Savings progress feteched Sucessfully", progressResponse);
        }
        catch(IllegalArgumentException e){
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }
    }