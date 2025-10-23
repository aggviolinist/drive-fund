package com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawal.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface WithdrawalsRepositoryCustom {
        BigDecimal findTotalWithdrawalAmountBySavingsPlan_PlanUuid(UUID planUuid);  
}
