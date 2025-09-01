package com.drivefundproject.drive_fund.catalogue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.dto.Request.CatalogueAddition;
import com.drivefundproject.drive_fund.model.Catalogue;
import com.drivefundproject.drive_fund.repository.CatalogueRepository;

@Service
@RequiredArgsConstructor
public class CatalogueService {

    private final CatalogueRepository catalogueRepository;

    public Catalogue addProduct(CatalogueAddition request) {

        // 1. Create the Admin object
        Catalogue newProduct = Catalogue.builder()
                .productname(request.getProductname())
                .productdesc(request.getProductdesc())
                .build();

        // 2. Save the car to the database and return it
        return catalogueRepository.save(newProduct);
    }
}