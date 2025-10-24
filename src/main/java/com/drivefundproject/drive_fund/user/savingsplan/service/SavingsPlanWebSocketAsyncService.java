package com.drivefundproject.drive_fund.user.savingsplan.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessageMappingInfo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.user.savingsplan.dto.response.SocketDepositDetailsResponse;
import com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.dto.response.SavingsProgressResponse;
import com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.service.SavingsDisplayService;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.dto.response.InterestResponse;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.dto.response.PaymentResponse;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.model.Payment;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class SavingsPlanWebSocketAsyncService {

    private final SimpMessagingTemplate messagingTemplate;
    private final PaymentService paymentService;
    private final SavingsDisplayService savingsDisplayService;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_PREFIX = "/topic/progress/";

    @Async 
    public void handleDepositAndPushUpdates(UUID planUuid, Payment newPayment, BigDecimal depositAmount){
        //1. Recalculate and Force Cache update(causes cache miss in Payment Service)
        try{
            //2. GET PROGRESS RESPONSE BEFORE INTEREST LOGIC forces data into cache
            SavingsProgressResponse savingsProgressResponse = savingsDisplayService.getSavingsProgress(planUuid);
            PaymentResponse paymentResponse = savingsDisplayService.createPaymentResponse(newPayment);

            //3. CHECK FOR INTEREST USING CURRENT PERCENTAGE
            double percentageCompleted = savingsProgressResponse.getPercentageCompleted();
            InterestResponse interestResponse = paymentService.calculateInterest(planUuid, percentageCompleted);

            //4. GET PROGRESS AFTER INTEREST WAS APPLIED
            if (interestResponse.getInterestAmount().compareTo(BigDecimal.ZERO) > 0){
                //Refresh progress response after interest was applied
                savingsProgressResponse = savingsDisplayService.getSavingsProgress(planUuid);

            }
            //5. BUILD COMBINED DTO
            SocketDepositDetailsResponse combinedDTO = new SocketDepositDetailsResponse(
                paymentResponse,
                savingsProgressResponse,
                interestResponse,
                "DEPOSIT OF $" + depositAmount + "recorded successfully. Progress updated in real time." + interestResponse.getMessage()
            );
            //6. PUSH UPDATES VIA SIMPMESSAGINGTEMPLATE
            String jsonPayload = objectMapper.writeValueAsString(combinedDTO);
            String destination = TOPIC_PREFIX + planUuid.toString();

            messagingTemplate.convertAndSend(destination, jsonPayload);
        }
        catch(JsonProcessingException e){
            System.err.println("Error processing JSON for plan" + planUuid + ":" + e.getMessage());
        }
        catch(Exception e){
            System.err.println("Unexpected error during async update for the plan" + planUuid + ":" + e.getMessage());
            e.printStackTrace();
        }
    }

    
}
