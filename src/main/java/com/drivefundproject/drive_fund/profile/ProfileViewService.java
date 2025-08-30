package com.drivefundproject.drive_fund.profile;

import org.springframework.stereotype.Service;
import com.drivefundproject.drive_fund.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import java.util.Optional;
import com.drivefundproject.drive_fund.model.User;
import com.drivefundproject.drive_fund.dto.UserProfileRequest;

@Service
@RequiredArgsConstructor
public class ProfileViewService {

    private final UserRepository userRepository;

    public UserProfileRequest getProfileViewDetails(Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return UserProfileRequest.builder()
                .firstname(user.getFirstname())
                .imageUrl(user.getImageUrl())
                .build();
        } else {
            throw new RuntimeException("User not found");
        }
    }
}