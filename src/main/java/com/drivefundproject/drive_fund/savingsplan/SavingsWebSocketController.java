package com.drivefundproject.drive_fund.savingsplan;

import java.util.UUID;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SavingsWebSocketController {
    private final PaymentService paymentService;
    private final SavingsDisplayService savingsDisplayService;
    private final ObjectMapper objectMapper; //Jackson json-object, object-json

    private final UUID MOCK_SAVINGS_PLAN_UUID = UUID.fromString(""); //TO DO
    
    @MessageMapping("/deposit")
    @SendTo("/topic/progress")
    public String handleDepositAndCalculateProgress(String message) throws JsonProcessingException{
        DepositMessage deposit;

        try{
            deposit = objectMapper.readValue(message, DepositMessage.class);
        }
        catch(Exception e){
            System.err.println("Error parsing deposit message:" + e.getMessage());
            return "{\"error\": \"Invalid deposit format.\"}";
        }
        BigDecimal depositAmount = deposit.getAmount();
        System.out.println("Received deposit of:" + depositAmount + "for Plan: " + MOCK_SAVINGS_PLAN_UUID);

        try{
            
        }
    }
}
