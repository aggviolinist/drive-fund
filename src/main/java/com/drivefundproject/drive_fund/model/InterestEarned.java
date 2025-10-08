package com.drivefundproject.drive_fund.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "interest_earned")
public class InterestEarned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "savings_plan_id", nullable = false)
    private SavingsPlan savingsPlan;

    @Column(name = "interestEarned_uuid", nullable = false , updatable = false)
    private UUID interestEarnedUuid;

    private BigDecimal interestAmount;

    private LocalDate dateInterestEarned;

    private String interestType;

    private String transactionId;

    @PrePersist
    public void prePersist(){
        if(this.interestEarnedUuid == null){
            this.interestEarnedUuid = UUID.randomUUID();
        }

    }
    
}
