package com.drivefundproject.drive_fund.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.drivefundproject.drive_fund.model.Withdrawals;

public interface WithdrawalsRepository extends JpaRepository<Withdrawals, Long> {
    //Get all cash from the withdrals table
    List<Withdrawals> findBySavingsPlan_PlanUuid(UUID planUuid);    
}
