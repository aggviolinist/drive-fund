package com.drivefundproject.drive_fund.savingsplan;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.drivefundproject.drive_fund.dto.Request.SocketPaymentRequest;
import com.drivefundproject.drive_fund.dto.Response.InterestResponse;
import com.drivefundproject.drive_fund.dto.Response.PaymentResponse;
import com.drivefundproject.drive_fund.dto.Response.SavingsProgressResponse;
import com.drivefundproject.drive_fund.dto.Response.SocketDepositDetailsResponse;
import com.drivefundproject.drive_fund.model.Payment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/sockets")
public class SavingsDisplayWebSocketController {
    private final PaymentService paymentService;
    private final SavingsDisplayService savingsDisplayService;
    private final ObjectMapper objectMapper; //Jackson json-java object, java object-json

    //private final UUID MOCK_SAVINGS_PLAN_UUID = UUID.fromString("db6dab4e-6524-4a84-8824-2ab2c472303e"); //TO DO

     @GetMapping("/messages/{planUuid}")
    public String getTemplate(@PathVariable UUID planUuid, Model model){
        model.addAttribute("planUuid", planUuid.toString());
        return "index";
    } //To be Deleted
    
    @MessageMapping("/deposit/{planUuid}")
    @SendTo("/topic/progress/{planUuid}")
    public String handleDepositAndCalculateProgress(@DestinationVariable UUID planUuid, @Payload SocketPaymentRequest depositRequest) throws JsonProcessingException{
        BigDecimal depositAmount = depositRequest.getAmount();
        System.out.println("Received deposit of:" + depositAmount + " for Plan:" + planUuid);
        
        try{
            // 1. Record User deposit
            Payment newPayment = paymentService.recordPaymentDeposit(planUuid, depositAmount);
            
            // 2. Get progress response BEFORE interest logic
            SavingsProgressResponse savingsProgressResponse = savingsDisplayService.getSavingsProgress(planUuid);

            PaymentResponse paymentResponse = savingsDisplayService.createPaymentResponse(newPayment);
 
            // 3. Check for Interest using current percentage
            double percentageCompleted = savingsProgressResponse.getPercentageCompleted();
            InterestResponse interestResponse = paymentService.calculateInterest(planUuid, percentageCompleted);

            // 4. Get progress AFTER interest has been applied
            if(interestResponse.getInterestAmount().compareTo(BigDecimal.ZERO) > 0){
                //We want to refresh the progress response after interest was applied
                savingsProgressResponse = savingsDisplayService.getSavingsProgress(planUuid);
            }
            //5. Build and return combined DTO
            SocketDepositDetailsResponse combinedDTO = new SocketDepositDetailsResponse(
                paymentResponse,
                savingsProgressResponse,
                interestResponse,
                "Deposit of $" + depositAmount + "recorded successfully. Progress updated in real-time." + interestResponse.getMessage());

            return objectMapper.writeValueAsString(combinedDTO);
        }
        catch(IllegalArgumentException e){
            System.err.println("Error processing savings logic: " + e.getMessage());

            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
        catch(Exception e){
            System.err.println("Internal Server Error" + e.getMessage());
            return "{\"error\": \"Internal server error during savings calculation.\"}";
        }
    }
}
