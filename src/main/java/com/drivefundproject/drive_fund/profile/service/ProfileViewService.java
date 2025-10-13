package com.drivefundproject.drive_fund.profile.service;


import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

import com.drivefundproject.drive_fund.auth.repository.UserRepository;
import com.drivefundproject.drive_fund.profile.dto.response.UserProfileResponse;

@Service
@RequiredArgsConstructor
public class ProfileViewService {

    private final UserRepository userRepository;

    public UserProfileResponse getProfileViewDetails(Integer userId) {
        //Optional<User> userOptional = userRepository.findById(userId);
        return userRepository.findById(userId)
          .map(user -> UserProfileResponse.builder()
                .firstname(user.getFirstname())
                .imageUrl(user.getImageUrl())
                .build())
                .orElseThrow(() -> new RuntimeException("User not found with ID " + userId));
        }
    }