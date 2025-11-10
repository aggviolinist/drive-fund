package com.drivefundproject.drive_fund.user.addsavingsplan.repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.drivefundproject.drive_fund.user.addsavingsplan.model.SavingsPlan;


@Repository
public interface SavingsPlanRepository extends JpaRepository<SavingsPlan, Integer> {
        List<SavingsPlan> findByUserId(Integer userId);
        Optional<SavingsPlan> findByUserIdAndCatalogueCatUuid(Integer userId, UUID catUuid); 
        Optional<SavingsPlan> findByPlanUuid(UUID planUuid);

        @Query("SELECT s FROM SavingsPlan s LEFT JOIN FETCH s.catalogue WHERE s.planUuid = :planUuid")
        
        Optional<SavingsPlan> findByPlanUuidWithCatalogueFetch(UUID planUuid);
         //Server Error: Error in the Catalogue#25] - no session



}
