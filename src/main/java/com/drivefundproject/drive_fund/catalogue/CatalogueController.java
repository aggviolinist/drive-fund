package com.drivefundproject.drive_fund.catalogue;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.drivefundproject.drive_fund.dto.Request.CatalogueAddition;
import com.drivefundproject.drive_fund.model.Catalogue;

@RestController
@RequestMapping("/api/v1/add-item")
@RequiredArgsConstructor
public class CatalogueController {

    private final CatalogueService catalogueService;

    @PostMapping("/add-stuff")
    public ResponseEntity<Catalogue> addProduct(@RequestBody CatalogueAddition catalogueAddition) {
        // Delegate all logic to the service layer
        Catalogue newProduct = catalogueService.addProduct(catalogueAddition);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }
}