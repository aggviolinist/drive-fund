package com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentRepositoryCustom  {
        BigDecimal findTotalPaymentAmountBySavingsPlan_PlanUuid(UUID planUuid);
}
