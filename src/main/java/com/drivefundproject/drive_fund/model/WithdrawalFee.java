package com.drivefundproject.drive_fund.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "withdrawal_fees")
public class WithdrawalFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "savings_plan_id", nullable = false)
    private SavingsPlan savingsPlan;

    @Column(name = "withdrawals_fee_uuid", nullable = false , updatable = false)
    private UUID withdrawalsFeeUuid;

    private BigDecimal feeAmount;

    private LocalDate dateWithdrawalFeeEarned;

    @Enumerated(EnumType.STRING)
    private String withdrawalType;

    private String transactionId;

    @PrePersist
    public void prePersist(){
        if(this.withdrawalsFeeUuid == null){
            this.withdrawalsFeeUuid = UUID.randomUUID();
        }

    }

    
}
