package com.drivefundproject.drive_fund.dto.Request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocketWithdrawalRequest {
    
    //Needs to be validated in front end
    @NotNull(message = "Withdrawal Amount is needed")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0")
    private BigDecimal withdrawnAmount;
}
