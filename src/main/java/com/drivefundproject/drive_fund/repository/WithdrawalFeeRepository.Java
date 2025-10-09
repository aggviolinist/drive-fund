package com.drivefundproject.drive_fund.repository;

import com.drivefundproject.drive_fund.model.WithdrawalFee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WithdrawalFeeRepository extends JpaRepository<WithdrawalFee, Long> {

}