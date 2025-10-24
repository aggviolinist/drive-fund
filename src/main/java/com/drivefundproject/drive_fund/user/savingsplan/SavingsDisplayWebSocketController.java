package com.drivefundproject.drive_fund.user.savingsplan;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.time.LocalDate;


import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.drivefundproject.drive_fund.user.savingsplan.dto.response.SocketDepositDetailsResponse;
import com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.dto.response.SavingsProgressResponse;
import com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.service.SavingsDisplayService;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.dto.response.InterestResponse;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.dto.request.SocketPaymentRequest;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.dto.response.PaymentResponse;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.model.Payment;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.service.PaymentService;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawal.dto.request.SocketWithdrawalRequest;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawal.dto.response.SocketWithdrawalResponse;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawal.service.WithdrawalService;
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

    private final WithdrawalService withdrawalService;

    //private final UUID MOCK_SAVINGS_PLAN_UUID = UUID.fromString("db6dab4e-6524-4a84-8824-2ab2c472303e"); //TO DO

     @GetMapping("/messages/{planUuid}")
    public String getTemplate(@PathVariable UUID planUuid, Model model){
        model.addAttribute("planUuid", planUuid.toString());
        return "index";
    } //To be Deleted
    
    @MessageMapping("/deposit/{planUuid}")
    //@SendTo("/topic/progress/{planUuid}")
    public void handleDepositAndCalculateProgress(@DestinationVariable UUID planUuid, @Payload SocketPaymentRequest depositRequest) throws JsonProcessingException{
        BigDecimal depositAmount = depositRequest.getPaymentAmount();
        System.out.println("Received deposit of:" + depositAmount + " for Plan:" + planUuid);
        
        try{
            // 1. Record User deposit
            paymentService.recordPaymentDeposit(planUuid, depositAmount);
            
            // 2. Get progress response BEFORE interest logic
            // SavingsProgressResponse savingsProgressResponse = savingsDisplayService.getSavingsProgress(planUuid);

            //PaymentResponse paymentResponse = savingsDisplayService.createPaymentResponse(newPayment);
 
            // 3. Check for Interest using current percentage
           // double percentageCompleted = savingsProgressResponse.getPercentageCompleted();
            // InterestResponse interestResponse = paymentService.calculateInterest(planUuid, percentageCompleted);

            // 4. Get progress AFTER interest has been applied
           // if(interestResponse.getInterestAmount().compareTo(BigDecimal.ZERO) > 0){
                //We want to refresh the progress response after interest was applied
              //  savingsProgressResponse = savingsDisplayService.getSavingsProgress(planUuid);
            //}
            //5. Build and return combined DTO
            // SocketDepositDetailsResponse combinedDTO = new SocketDepositDetailsResponse(
            //     paymentResponse,
            //     savingsProgressResponse,
            //     interestResponse,
            //     "Deposit of $" + depositAmount + "recorded successfully. Progress updated in real-time." + interestResponse.getMessage());

            //return objectMapper.writeValueAsString(combinedDTO);
        }
        catch(IllegalArgumentException e){
            e.printStackTrace();
            System.err.println("Error processing savings logic: " + e.getMessage());
            //return "{\"error\": \"" + e.getMessage() + "\"}";
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println("Internal Server Error" + e.getMessage());
            //return "{\"error\": \"Internal server error during savings calculation.\"}";
        }
    }
    @MessageMapping("/withdrawal/{planUuid}")
    @SendTo("/topic/progress/{planUuid}")
    public String handleWithdrawalAndCalculateProgress(@DestinationVariable UUID planUuid, @Payload SocketWithdrawalRequest withdrawalRequest) throws JsonProcessingException{
        BigDecimal withdrawnAmount = withdrawalRequest.getWithdrawnAmount();
        System.out.println("Withdrawal Amount of:" + withdrawnAmount + "for plan:" + planUuid);

        try{
            //1. Record the user withthdrawal together with the fees and penalties associated with it
            withdrawalService.recordWithdrawal(planUuid, withdrawnAmount);

            //2. Get the updated progress response
            //This reduces the totaldeposits, totalExpectedAmounts, Percentages
            SavingsProgressResponse savingsProgressService = savingsDisplayService.getSavingsProgress(planUuid);

            //3. Build and return the combined CTO
            String successMessage = "Withdrawal of $" + withdrawnAmount.setScale(2,RoundingMode.HALF_UP) + " processed successfully. Progress updated in real-time.";
            LocalDate withdrawnDate = LocalDate.now();
            SocketWithdrawalResponse socketWithdrawalResponseDTO = new SocketWithdrawalResponse(
                savingsProgressService,
                withdrawnAmount,
                withdrawnDate,
                successMessage
            );
            return objectMapper.writeValueAsString(socketWithdrawalResponseDTO);
        }
        catch(IllegalArgumentException e){
            System.out.println("Error processing withdrawal logic: " + e.getMessage());

            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
        catch(Exception e){
            System.err.println("Internal Server Error" + e.getMessage());
            return "{\"error\": \"Internal server error during withdrawal processing. \"}";
        }
    }
}
