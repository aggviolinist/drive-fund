package com.drivefundproject.drive_fund.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.web.server.ServerSecurityMarker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.auth.image.S3Service;
import com.drivefundproject.drive_fund.dto.AuthenticationResponse;
import com.drivefundproject.drive_fund.dto.RegisterRequest;
import com.drivefundproject.drive_fund.dto.loginRequest;
import com.drivefundproject.drive_fund.jwt.JwtService;
import com.drivefundproject.drive_fund.model.Role;
import com.drivefundproject.drive_fund.model.User;
import com.drivefundproject.drive_fund.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final S3Service s3Service;

    public AuthenticationResponse register(RegisterRequest request) {
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
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'register'");
    }

public AuthenticationResponse login(loginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail())
      .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
    // TODO Auto-generated method stub
    //throw new UnsupportedOperationException("Unimplemented method 'login'");
}
    
}
