package com.drivefundproject.drive_fund.admin.fetchSavingsPlan.controller;




import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drivefundproject.drive_fund.admin.fetchSavingsPlan.service.FetchSavingsPlanService;
import com.drivefundproject.drive_fund.user.addsavingsplan.dto.response.ConciseSavingsPlanResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/getsavings")
@PreAuthorize("hasRole('ADMIN')") // Only Admins can access this
@RequiredArgsConstructor
public class FetchSavingsPlanController {

    private final FetchSavingsPlanService fetchSavingsPlanService;

    @GetMapping("/all")
    public ResponseEntity<List<ConciseSavingsPlanResponse>> getAllSavingsPlans() {
        // The service method will handle fetching all plans and mapping them to the DTO.
        return ResponseEntity.ok(fetchSavingsPlanService.getAllSavingsPlans());
    }
}