package com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawal.controller;

import com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.dto.response.SavingsProgressResponse;
import com.drivefundproject.drive_fund.user.savingsplan.restsavingsDetailsAndCheckout.service.SavingsDisplayService;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawal.dto.request.SocketWithdrawalRequest;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawal.dto.response.SocketWithdrawalResponse;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawal.service.WithdrawalService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/savings")
@RequiredArgsConstructor
public class WithdrawalController {

    private final WithdrawalService withdrawalService;
    private final SavingsDisplayService savingsDisplayService;

    @PostMapping("/withdraw/{planUuid}")
    public ResponseEntity<?> withdraw(@PathVariable UUID planUuid, 
                                      @RequestBody SocketWithdrawalRequest request) {
        
        BigDecimal withdrawnAmount = request.getWithdrawnAmount();

        if (withdrawnAmount == null || withdrawnAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("{\"error\": \"Invalid withdrawal amount provided.\"}");
        }

        System.out.println("Withdrawal Request: $" + withdrawnAmount + " for plan: " + planUuid);

        try {
            // 1. Record the withdrawal (This handles all fee/penalty logic)
            withdrawalService.recordWithdrawal(planUuid, withdrawnAmount);

            // 2. Get the updated progress response
            SavingsProgressResponse savingsProgressResponse = 
                savingsDisplayService.getSavingsProgress(planUuid);

            // 3. Build and return the final response DTO
            String successMessage = "Withdrawal of $" + 
                withdrawnAmount.setScale(2, RoundingMode.HALF_UP) + 
                " processed successfully.";
            
            SocketWithdrawalResponse response = new SocketWithdrawalResponse(
                savingsProgressResponse,
                withdrawnAmount,
                LocalDate.now(),
                successMessage
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // Handle business logic errors (e.g., insufficient balance, plan not found)
            System.out.println("Error processing withdrawal: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            // Catch unexpected errors
            System.err.println("Internal Server Error during withdrawal: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("{\"error\": \"Internal server error during withdrawal processing.\"}");
        }
    }
}