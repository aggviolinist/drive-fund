package com.drivefundproject.drive_fund.model;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.cglib.core.Local;

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
@Table(name = "user_savings")
public class SavingsPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private UUID planUuid;

    private Double amount;
    private Integer timeline; 

    private LocalDate creationDate;
    private LocalDate targetCompletionDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Frequency frequency;
    
    //Establishing a many to one relationship between savingsplan and catalogue,user 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalogue_id")
    private Catalogue catalogue ;

    //The user who made the savings plan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //Automatically generates uuid before plan is created
    @PrePersist
    public void generateUuid(){
        if(this.planUuid == null){
            this.planUuid = UUID.randomUUID();
        }
    }

    public UUID getPlan_uuid() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPlan_uuid'");
    }

}
