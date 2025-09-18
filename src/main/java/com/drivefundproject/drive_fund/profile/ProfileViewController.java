package com.drivefundproject.drive_fund.profile;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drivefundproject.drive_fund.dto.Response.ResponseHandler;
import com.drivefundproject.drive_fund.dto.Response.UserProfileResponse;
import com.drivefundproject.drive_fund.model.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class ProfileViewController {

    private final ProfileViewService profileViewService;

    @GetMapping("/view/profile")
    public ResponseEntity<Object> getUserProfileDetails() {
        //Lets get the authenticated user from the SecurityContextHolder @PathVariable Integer userId
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Integer userId = currentUser.getId();   
         
        UserProfileResponse response = profileViewService.getProfileViewDetails(userId);
        return ResponseHandler.generateResponse(HttpStatus.OK, "User profile fetched successfully",response);
    }
}