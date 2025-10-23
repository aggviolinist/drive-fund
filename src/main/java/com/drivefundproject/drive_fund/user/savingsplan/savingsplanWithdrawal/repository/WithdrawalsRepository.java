package com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawal.repository;



import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawal.model.Withdrawals;


@Repository
public interface WithdrawalsRepository extends JpaRepository<Withdrawals, Long>, WithdrawalsRepositoryCustom {
    //Get all cash from the withdrals table
    List<Withdrawals> findBySavingsPlan_PlanUuid(UUID planUuid);   
    //BigDecimal findTotalWithdrawalAmountBySavingsPlan_PlanUuid(UUID planUuid); 
}
