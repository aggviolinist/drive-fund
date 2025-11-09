package com.drivefundproject.drive_fund.admin.fetchUsers.service;



import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.auth.model.User;
import com.drivefundproject.drive_fund.auth.repository.UserRepository;
import com.drivefundproject.drive_fund.user.catalogue.model.Catalogue;
import com.drivefundproject.drive_fund.admin.fetchUsers.dto.response.FetchUserResponse;
import com.drivefundproject.drive_fund.admin.fetchUsers.repository.FetchUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FetchUserService {
    private final FetchUserRepository fetchUserRepository;

    public List<FetchUserResponse> getAllUsers(){
        return fetchUserRepository.findAll()
               .stream()
               .map(user -> FetchUserResponse.builder()
                   .firstname(user.getFirstname())
                   .lastname(user.getLastname())
                   .email(user.getEmail())
                .build())
            .toList();
    }    

}
