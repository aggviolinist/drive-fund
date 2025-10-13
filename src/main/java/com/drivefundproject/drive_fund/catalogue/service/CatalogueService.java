package com.drivefundproject.drive_fund.catalogue.service;


import lombok.RequiredArgsConstructor;

import java.util.List;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.catalogue.dto.request.CatalogueRequest;
import com.drivefundproject.drive_fund.catalogue.model.Catalogue;
import com.drivefundproject.drive_fund.catalogue.repository.CatalogueRepository;


@Service
@RequiredArgsConstructor
public class CatalogueService {

    private final CatalogueRepository catalogueRepository;
//Adding the product. Both Admin and user can add a product
    public Catalogue addProduct(CatalogueRequest request) {
        if(catalogueRepository.findByProductname(request.getProductname()).isPresent()){
            throw new RuntimeException("This product already exists. Please enter another product.");
        }
        // 1. Create the Admin object
        Catalogue newProduct = Catalogue.builder()
                .productname(request.getProductname())
                .productdesc(request.getProductdesc())
                .build();
        try{

        // 2. Save the car to the database and return it
        return catalogueRepository.save(newProduct);
    } catch(DataIntegrityViolationException e)
    {
        throw new RuntimeException("A product with this name already exists.",e);
    }
    }
//Viewing all products
    public List<Catalogue> viewAllProducts(){
        return catalogueRepository.findAll();
    }
}