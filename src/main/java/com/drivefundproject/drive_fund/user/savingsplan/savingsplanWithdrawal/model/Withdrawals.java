package com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawal.model;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.drivefundproject.drive_fund.user.addsavingsplan.model.SavingsPlan;
import com.drivefundproject.drive_fund.user.savingsplan.savingsplanWithdrawalFee.model.WithdrawalType;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "withdrals")
public class Withdrawals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "savings_plan_id", nullable = false)
    private SavingsPlan savingsPlan;

    @Column(name = "withdrawals_uuid", nullable = false , updatable = false)
    private UUID withdrawalsUuid;

    private BigDecimal withdrawalAmount;

    private LocalDate dateWithdrawn;

    @Enumerated(EnumType.STRING)
    @Column(name = "withdrawal_type", nullable = false)
    private WithdrawalType withdrawalType;

    private String systemMessage;

    private String transactionId;

    @PrePersist
    public void prePersist(){
        if(this.withdrawalsUuid == null){
            this.withdrawalsUuid = UUID.randomUUID();
        }

    }

    
}
