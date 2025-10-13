package com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawalFee.repository;


import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawalFee.model.WithdrawalFee;


@Repository
public interface WithdrawalFeeRepository extends JpaRepository<WithdrawalFee, Long> {

    List<WithdrawalFee> findBySavingsPlan_PlanUuid(UUID planUuid);
}