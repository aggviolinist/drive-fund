package com.drivefundproject.drive_fund.dto.Request;

import org.springframework.web.multipart.MultipartFile;

import com.drivefundproject.drive_fund.auth.PasswordMatches;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@PasswordMatches 
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    private String firstname;

    @NotBlank(message = "Last name is required")
    private String lastname;

    @NotNull(message = "Your Image is required") 
    private MultipartFile profileImage;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid") // This is our auth principal
    private String email;

    @NotBlank(message = "Password is required")  
    private String password; 

     @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;

}
