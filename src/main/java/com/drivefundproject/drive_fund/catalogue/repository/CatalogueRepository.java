package com.drivefundproject.drive_fund.catalogue.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivefundproject.drive_fund.catalogue.model.Catalogue;

import java.util.UUID;
    
@Repository
public interface CatalogueRepository extends JpaRepository<Catalogue, Integer> {
   Optional <Catalogue> findByProductname(String productname);  
   Optional <Catalogue> findByCatUuid(UUID catUuid); 
}
