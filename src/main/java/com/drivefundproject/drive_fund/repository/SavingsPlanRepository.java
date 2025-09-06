package com.drivefundproject.drive_fund.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivefundproject.drive_fund.model.SavingsPlan;

@Repository
public interface SavingsPlanRepository extends JpaRepository<SavingsPlan, Integer> {
        List<SavingsPlan> findByUserId(Integer userId);
}
