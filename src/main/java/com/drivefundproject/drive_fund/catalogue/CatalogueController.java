package com.authorizen.userauthh.admin;

import com.authorizen.userauthh.dto.CarRequest;
import com.authorizen.userauthh.model.Admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/add-item")
@RequiredArgsConstructor
public class AdminController {

    private final CatalogueService catalogueService;

    @PostMapping("/add-stuff")
    public ResponseEntity<Admin> addCar(@ModelAttribute CarRequest carRequest) {
        // Delegate all logic to the service layer
        Admin newCar = adminService.addCar(carRequest);
        return new ResponseEntity<>(newCar, HttpStatus.CREATED);
    }
}