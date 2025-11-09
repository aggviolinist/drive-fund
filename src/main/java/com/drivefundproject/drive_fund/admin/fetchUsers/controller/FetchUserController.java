package com.drivefundproject.drive_fund.admin.fetchUsers.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drivefundproject.drive_fund.admin.fetchUsers.dto.response.FetchUserResponse;
import com.drivefundproject.drive_fund.admin.fetchUsers.service.FetchUserService;
import com.drivefundproject.drive_fund.auth.model.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class FetchUserController {

private final FetchUserService fetchUserService;

    @GetMapping
    public ResponseEntity<List<FetchUserResponse>> getAllUsers() {
        return ResponseEntity.ok(fetchUserService.getAllUsers());
    }
}
