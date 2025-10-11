package com.drivefundproject.drive_fund.catalogue;


import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.drivefundproject.drive_fund.dto.Request.CatalogueRequest;
import com.drivefundproject.drive_fund.dto.Response.CatalogueResponse;
import com.drivefundproject.drive_fund.dto.Response.CatalogueViewAllResponse;
import com.drivefundproject.drive_fund.dto.Response.ResponseHandler;
import com.drivefundproject.drive_fund.model.Catalogue;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/product")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class CatalogueController {

    private final CatalogueService catalogueService;

    @PostMapping("/add-products")
    public ResponseEntity<Object> addProduct(@Valid @RequestBody CatalogueRequest catalogueAddition) {
        try{
        // Delegate all logic to the service layer
        Catalogue newProduct = catalogueService.addProduct(catalogueAddition);
        //mapping out exactly the data w want displayed
        CatalogueResponse catalogueResponse = CatalogueResponse.builder()
         .catUuid(newProduct.getCatUuid())
         .productname(newProduct.getProductname())
         .productdesc(newProduct.getProductdesc())
         .build();

        return ResponseHandler.generateResponse(HttpStatus.CREATED,"Product added successfully",catalogueResponse);
    } catch(RuntimeException e){

        return ResponseHandler.generateResponse(HttpStatus.CONFLICT,"This entry already exists in the database", null);

    }
}

    @GetMapping("/view-all-products")
    public ResponseEntity<Object> viewAllProducts() { 
        List<Catalogue> allProducts = catalogueService.viewAllProducts();

       List<CatalogueViewAllResponse> catalogueViewAllResponse = allProducts.stream()
         .map(product -> CatalogueViewAllResponse.builder()
          .catUuid(product.getCatUuid())
          .productname(product.getProductname())
          .productdesc(product.getProductdesc())
          .build())
        .collect(Collectors.toList());

        return ResponseHandler.generateResponse(HttpStatus.OK,"Begin to save now!",catalogueViewAllResponse);
   }
}