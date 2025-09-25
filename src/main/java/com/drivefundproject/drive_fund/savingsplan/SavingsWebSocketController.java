package com.drivefundproject.drive_fund.savingsplan;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.drivefundproject.drive_fund.dto.Response.SavingsProgressResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
//@CrossOrigin("*")
@RequestMapping("/web/sockets")
public class SavingsWebSocketController {
    private final PaymentService paymentService;
    private final SavingsDisplayService savingsDisplayService;
    private final ObjectMapper objectMapper; //Jackson json-object, object-json

    private final UUID MOCK_SAVINGS_PLAN_UUID = UUID.fromString("db6dab4e-6524-4a84-8824-2ab2c472303e"); //TO DO

     @GetMapping("/messages")
    public String getTemplate(Model model){
        return "index";
    }
    
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
            paymentService.recordPaymentDeposit(MOCK_SAVINGS_PLAN_UUID, depositAmount);
            
            SavingsProgressResponse progressResponse = savingsDisplayService.getSavingsProgress(MOCK_SAVINGS_PLAN_UUID);

            return objectMapper.writeValueAsString(progressResponse);


        }
        catch(Exception e){
            System.err.println("Error processing savings logic: " + e.getMessage());

            return "{\"error\": \"Error in the savings calculation: " + e.getMessage() + "\"}";
        }
    }

    public static class DepositMessage{
        private BigDecimal amount;

        public BigDecimal getAmount(){
            return amount;
        }
        public void setAmount(BigDecimal amount){
            this.amount = amount;
        }
    }
}
