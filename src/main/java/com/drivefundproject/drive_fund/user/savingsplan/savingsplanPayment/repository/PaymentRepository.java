package com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.repository;

import java.lang.StackWalker.Option;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivefundproject.drive_fund.user.savingsplan.savingsplanPayment.model.Payment;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryCustom{
     List<Payment> findBySavingsPlan_PlanUuidOrderByPaymentDateAsc(UUID planUuid);
     Optional<Payment> findTopBySavingsPlan_PlanUuidOrderByPaymentDateDesc(UUID planUUid);  
     long countBySavingsPlan_PlanUuid(UUID planUuid); 

     //we removed the payment from here and created a custom JPA file
     //BigDecimal findTotalPaymentAmountBySavingsPlan_PlanUuid(UUID planUuid);

}