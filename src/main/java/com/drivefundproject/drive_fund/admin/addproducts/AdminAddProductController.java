package com.drivefundproject.drive_fund.admin.addproducts;


import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.drivefundproject.drive_fund.catalogue.dto.request.CatalogueRequest;
import com.drivefundproject.drive_fund.catalogue.dto.response.CatalogueResponse;
import com.drivefundproject.drive_fund.catalogue.model.Catalogue;
import com.drivefundproject.drive_fund.catalogue.service.CatalogueService;
import com.drivefundproject.drive_fund.exception.ResponseHandler;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/addproducts")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminAddProductController {

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
}