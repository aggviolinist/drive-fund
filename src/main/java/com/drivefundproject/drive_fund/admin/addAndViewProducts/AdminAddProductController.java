package com.drivefundproject.drive_fund.admin.addAndViewProducts;


import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.drivefundproject.drive_fund.exception.ResponseHandler;
import com.drivefundproject.drive_fund.user.catalogue.dto.request.CatalogueRequest;
import com.drivefundproject.drive_fund.user.catalogue.dto.response.CatalogueResponse;
import com.drivefundproject.drive_fund.user.catalogue.dto.response.CatalogueViewAll;
import com.drivefundproject.drive_fund.user.catalogue.model.Catalogue;
import com.drivefundproject.drive_fund.user.catalogue.service.CatalogueService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminAddProductController {

    private final CatalogueService catalogueService;
    
    @PreAuthorize("hasRole('ADMIN')")  
    @PostMapping("/add-products")
    public ResponseEntity<Object> addProduct(@Valid @RequestBody CatalogueRequest catalogueAddition) {
        try{
        // Delegate all logic to the service layer
        Catalogue newProduct = catalogueService.addProduct(catalogueAddition);
        //mapping out exactly the data w want displayed
        CatalogueResponse catalogueResponse = CatalogueResponse.builder()
         .catUuid(newProduct.getCatUuid())
         .productImageUrl(newProduct.getProductImageUrl())
         .productname(newProduct.getProductname())
         .productdesc(newProduct.getProductdesc())
         .build();

        return ResponseHandler.generateResponse(HttpStatus.CREATED,"Product added successfully",catalogueResponse);
    } catch(RuntimeException e){

        return ResponseHandler.generateResponse(HttpStatus.CONFLICT,"This entry already exists in the database", null);

    }
}
@PreAuthorize("isAuthenticated()")
@GetMapping("/view-all-products")
    public ResponseEntity<Object> viewAllProducts() { 
        List<Catalogue> allProducts = catalogueService.viewAllProducts();

       List<CatalogueViewAll> catalogueViewAllResponse = allProducts.stream()
         .map(product -> CatalogueViewAll.builder()
          .catUuid(product.getCatUuid())
          .productImageUrl(product.getProductImageUrl())
          .productname(product.getProductname())
          .productdesc(product.getProductdesc())
          .build())
        .collect(Collectors.toList());

        return ResponseHandler.generateResponse(HttpStatus.OK,"Begin to save now!",catalogueViewAllResponse);
   }
}