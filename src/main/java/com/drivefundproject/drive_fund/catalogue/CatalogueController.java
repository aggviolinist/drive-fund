package com.drivefundproject.drive_fund.catalogue;


import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.drivefundproject.drive_fund.dto.Request.CatalogueRequest;
import com.drivefundproject.drive_fund.dto.Response.ResponseHandler;
import com.drivefundproject.drive_fund.model.Catalogue;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class CatalogueController {

    private final CatalogueService catalogueService;

    @PostMapping("/add-products")
    public ResponseEntity<Object> addProduct(@RequestBody CatalogueRequest catalogueAddition) {
        // Delegate all logic to the service layer
        Catalogue newProduct = catalogueService.addProduct(catalogueAddition);
        return ResponseHandler.generateResponse(HttpStatus.CREATED,"Product added successfully",newProduct);
    }

    @GetMapping("/view-all-products")
    public ResponseEntity<Object> viewAllProducts() { 
        List<Catalogue> allProducts = catalogueService.viewAllProducts();
        return ResponseHandler.generateResponse(HttpStatus.OK,"Begin to save now!",allProducts);
   }
}