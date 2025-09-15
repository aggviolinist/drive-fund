package com.drivefundproject.drive_fund.repository;

import java.lang.StackWalker.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivefundproject.drive_fund.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
     Optional<Payment> findBySavingsPlan_PlanUuidOrderByPaymentDateAsc(UUID planUuid);
     Optional<Payment> findTopBySavingsPlan_PlanUuidOrderByPaymentDateDesc(UUID planUUid);  
     long countBySavingsPlan_PlanUuid(UUID planUuid);   
}