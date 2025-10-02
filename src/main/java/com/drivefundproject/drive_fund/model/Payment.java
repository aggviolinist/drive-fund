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
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "payment_uuid", nullable = false , updatable = false)
    private UUID paymentUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "savings_plan_id", nullable = false)
    private SavingsPlan savingsPlan;

    private BigDecimal Amount;

    private LocalDate paymentDate;

    private String systemMessage;

    private LocalDate withdrawalDate; 

    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType = PaymentType.DEPOSIT;

    @Enumerated(EnumType.STRING)
    @Column(name="withdrawal_type", nullable = false)
    private WithdrawalType withdrawalType = WithdrawalType.NO_WITHDRAWAL;

    @PrePersist
    public void prePersist(){
        if(this.paymentUuid == null){
            this.paymentUuid = UUID.randomUUID();
        }

    }
}
