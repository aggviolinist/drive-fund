package com.drivefundproject.drive_fund.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drivefundproject.drive_fund.dto.Request.RegisterRequest;
import com.drivefundproject.drive_fund.dto.Request.loginRequest;
import com.drivefundproject.drive_fund.dto.Response.ResponseHandler;
import com.drivefundproject.drive_fund.dto.Response.TokenResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private AuthenticationService service;
     public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(
        @Valid
        @ModelAttribute RegisterRequest request
    ){
       // return service.register(request);
       TokenResponse tokenResponse = service.register(request);
       return ResponseHandler.generateResponse(HttpStatus.CREATED,"Registration Successfull", tokenResponse);

    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(
        @RequestBody loginRequest request
    ){
        TokenResponse tokenResponse = service.login(request);
        return ResponseHandler.generateResponse(HttpStatus.OK,"Login Successful",tokenResponse);
       // return service.login(request);
       //return ResponseEntity.ok(service.login(request));


    }
    
}
