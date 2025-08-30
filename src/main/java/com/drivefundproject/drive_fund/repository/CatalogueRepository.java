package com.drivefundproject.drive_fund.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.drivefundproject.drive_fund.model.Catalogue;
    
@Repository
public interface CatalogueRepository extends JpaRepository<Catalogue, Integer> {

}
