package com.drivefundproject.drive_fund.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivefundproject.drive_fund.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    static List<Payment> findBySavingsPlan_PlanUuidOrderByPaymentDateAsc(UUID planUuid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findBySavingsPlan_PlanUuidOrderByPaymentDateAsc'");
    }
}