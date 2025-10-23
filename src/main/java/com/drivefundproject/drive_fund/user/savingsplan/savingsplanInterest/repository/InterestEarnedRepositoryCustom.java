package com.drivefundproject.drive_fund.user.savingsplan.savingsplanInterest.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface InterestEarnedRepositoryCustom {
     BigDecimal findTotalInterestAmountBySavingsPlan_PlanUuid(UUID planUuid);       
}
