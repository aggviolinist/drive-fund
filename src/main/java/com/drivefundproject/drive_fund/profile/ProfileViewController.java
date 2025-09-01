package com.drivefundproject.drive_fund.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drivefundproject.drive_fund.dto.Request.UserProfileRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class ProfileViewController {

    private final ProfileViewService profileViewService;

    @GetMapping("/view/profile/{userId}")
    public ResponseEntity<UserProfileRequest> getUserProfileDetails(
            @PathVariable Integer userId) {
        UserProfileRequest response = profileViewService.getProfileViewDetails(userId);
        return ResponseEntity.ok(response);
    }
}