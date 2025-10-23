package com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawal.repository;

import java.math.BigDecimal;
import java.util.UUID;

import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.model.Payment;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawal.model.Withdrawals;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class WithdrawalsRepositoryImpl implements WithdrawalsRepositoryCustom  {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BigDecimal findTotalWithdrawalAmountBySavingsPlan_PlanUuid(UUID planUuid) {
         CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        //Tell JPA the query returns a BigDecimal, not a Payment
        CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        Root<Withdrawals> root = query.from(Withdrawals.class);

        query.select(cb.sum(root.get("withdrawalAmount")))
             .where(cb.equal(root.get("savingsPlan").get("planUuid"), planUuid));

        BigDecimal result = entityManager.createQuery(query).getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
    
}
