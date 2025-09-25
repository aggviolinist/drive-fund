package com.drivefundproject.drive_fund.dto.Request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocketPaymentRequest{

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0")
        private BigDecimal amount;

        // public BigDecimal getAmount(){
        //     return amount;
        // }
        // public void setAmount(BigDecimal amount){
        //     this.amount = amount;
        // }
    }