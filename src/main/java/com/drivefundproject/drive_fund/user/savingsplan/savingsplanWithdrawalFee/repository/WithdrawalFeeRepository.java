package com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawalFee.repository;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawalFee.model.WithdrawalFee;


@Repository
public interface WithdrawalFeeRepository extends JpaRepository<WithdrawalFee, Long>, WithdrawalFeeRepositoryCustom {

    List<WithdrawalFee> findBySavingsPlan_PlanUuid(UUID planUuid);
    //BigDecimal findTotalFeeAmountBySavingsPlan_PlanUuid(UUID planUuid);
}