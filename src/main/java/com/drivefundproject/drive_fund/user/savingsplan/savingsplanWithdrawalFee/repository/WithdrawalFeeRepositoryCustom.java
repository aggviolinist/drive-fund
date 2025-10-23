package com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawalFee.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface WithdrawalFeeRepositoryCustom {
       BigDecimal findTotalFeeAmountBySavingsPlan_PlanUuid(UUID planUuid);
}
