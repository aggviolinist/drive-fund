package com.drivefundproject.drive_fund.savingsplan;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.drivefundproject.drive_fund.dto.Request.SocketPaymentRequest;
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

    private final UUID MOCK_SAVINGS_PLAN_UUID = UUID.fromString("db6dab4e-6524-4a84-8824-2ab2c472303e"); //TO DO

     @GetMapping("/messages")
    public String getTemplate(Model model){
        return "index";
    }
    
    @MessageMapping("/deposit")
    @SendTo("/topic/progress")
    public String handleDepositAndCalculateProgress(String message) throws JsonProcessingException{
        SocketPaymentRequest depositRequest;

        try{
            depositRequest = objectMapper.readValue(message, SocketPaymentRequest.class);
        }
        catch(Exception e){
            System.err.println("Error parsing deposit message:" + e.getMessage());
            return "{\"error\": \"Invalid deposit format or missing amount.\"}";
        }
        BigDecimal depositAmount = depositRequest.getAmount();
       // System.out.println("Received deposit of:" + depositAmount + "for Plan: " + MOCK_SAVINGS_PLAN_UUID);

        try{
            Payment newPayment = paymentService.recordPaymentDeposit(MOCK_SAVINGS_PLAN_UUID, depositAmount);
            
            SavingsProgressResponse progressResponse = savingsDisplayService.getSavingsProgress(MOCK_SAVINGS_PLAN_UUID);

            PaymentResponse paymentResponse = new PaymentResponse(
               newPayment.getSavingsPlan().getPlanUuid(),
               newPayment.getPaymentUuid(),
               newPayment.getAmount(),
               newPayment.getPaymentDate(),
               newPayment.getPaymentMethod(),
               newPayment.getTransactionId(),
               newPayment.getSavingsPlan().getStatus());

            SocketDepositDetailsResponse combinedDTO = new SocketDepositDetailsResponse(
                paymentResponse,
                progressResponse,
                "Deposit of $" + depositAmount + "recorded successfully. Progress updated in real-time. ");

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
