package com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.repository;



import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.model.InterestEarned;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.model.InterestType;



public interface InterestEarnedRepository extends JpaRepository<InterestEarned, Long> {
    //Get all interest earned by specific savings plan
    List<InterestEarned> findBySavingsPlan_PlanUuid(UUID planUuid);
    //Check if interest of specific type has been earned for a specific savings plan
    long  countBySavingsPlan_PlanUuidAndInterestType(UUID planUuid, InterestType interestType);
    BigDecimal findTotalInterestAmountBySavingsPlan_PlanUuid(UUID planUuid);    
}
