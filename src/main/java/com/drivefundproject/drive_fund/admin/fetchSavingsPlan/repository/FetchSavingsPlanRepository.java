package com.drivefundproject.drive_fund.admin.fetchSavingsPlan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivefundproject.drive_fund.user.addsavingsplan.model.SavingsPlan;

@Repository
public interface FetchSavingsPlanRepository extends JpaRepository<SavingsPlan, Integer> {

    @EntityGraph(attributePaths = {"user", "catalogue"})
    List<SavingsPlan> findAll();
    
}