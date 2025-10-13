package com.drivefundproject.drive_fund.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class loginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid") // This is our auth principal
    private String email;
    
    @NotBlank(message = "Password is required")  
    String password;
}
