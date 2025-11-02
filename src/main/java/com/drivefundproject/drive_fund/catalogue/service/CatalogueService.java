package com.drivefundproject.drive_fund.catalogue.service;


import lombok.RequiredArgsConstructor;

import java.util.List;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.catalogue.dto.request.CatalogueRequest;
import com.drivefundproject.drive_fund.catalogue.model.Catalogue;
import com.drivefundproject.drive_fund.catalogue.repository.CatalogueRepository;
import com.drivefundproject.drive_fund.utilities.profileImages.S3Service;


@Service
@RequiredArgsConstructor
public class CatalogueService {

    private final CatalogueRepository catalogueRepository;
    private final S3Service s3Service;

//Adding the product. Both Admin and user can add a product
    public Catalogue addProduct(CatalogueRequest request) {
        if(catalogueRepository.findByProductname(request.getProductname()).isPresent()){
            throw new RuntimeException("This product already exists. Please enter another product.");
        }
        // 1. Create the Admin object
        String productImageUrl = s3Service.uploadFile(request.getProductImage());

        Catalogue newProduct = Catalogue.builder()
                .productImageUrl(productImageUrl)
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