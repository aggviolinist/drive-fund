package com.drivefundproject.drive_fund.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.auth.dto.request.RegisterRequest;
import com.drivefundproject.drive_fund.auth.dto.request.loginRequest;
import com.drivefundproject.drive_fund.auth.dto.response.TokenResponse;
import com.drivefundproject.drive_fund.auth.model.User;
import com.drivefundproject.drive_fund.auth.repository.UserRepository;
import com.drivefundproject.drive_fund.config.jwt.JwtService;
import com.drivefundproject.drive_fund.utilities.profileImages.S3Service;
import com.drivefundproject.drive_fund.utilities.roles.Role;

import lombok.RequiredArgsConstructor;




@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final S3Service s3Service;

    public TokenResponse register(RegisterRequest request) {
        // Check if a user with this email already exists
    if (repository.findByEmail(request.getEmail()).isPresent()) {
        // If an account with the email is found, throw an exception
        // This prevents creating a duplicate account
        throw new IllegalArgumentException("Email already in use.");
    }
    //Image wasn't a must
    // String imageUrl = null;

    //  if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
    //         imageUrl = s3Service.uploadFile(request.getProfileImage());
    //     }
      //image is mandatory now
        String imageUrl = s3Service.uploadFile(request.getProfileImage());

        var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .imageUrl(imageUrl)
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.USER)
        .build();
    repository.save(user);
    var jwtToken = jwtService.generateToken(user);
// Moving this to controller seperation of concerns principle -------->TokenResponse tokenResponse = TokenResponse.builder()
    return TokenResponse.builder()
    .token(jwtToken)
    .role(user.getRole())
    .firstname(user.getFirstname())
    .build();
    // return TokenResponse.builder()
    //     .token(jwtToken)
    //     .build();
//Moving this to Controller seperation of concerns principle ------>>return ResponseHandler.generateResponse(HttpStatus.CREATED,"Registration Successful", tokenResponse);
    }

public TokenResponse login(loginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail())
      .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
//Moving this to Controller seperation of concerns principle ----------->> TokenResponse tokenResponse = TokenResponse.builder()
    return TokenResponse.builder()
    .token(jwtToken)
    .role(user.getRole())
    .firstname(user.getFirstname())
    .build();

    // return TokenResponse.builder()
    //     .token(jwtToken)
    //     .build();
//Moving this to controller for seperation of concerns principle ------------------->>return ResponseHandler.generateResponse(HttpStatus.OK,"Login Successful",tokenResponse);
}   
}