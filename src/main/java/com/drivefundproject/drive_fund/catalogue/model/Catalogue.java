package com.drivefundproject.drive_fund.catalogue.model;



import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Builder
@Table(name = "catalogue")
public class Catalogue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cat_uuid")
    private UUID catUuid; 

    @Column(unique = true)
    private String productname;

    private String productdesc;

     //Automatically generates uuid before product is created
    @PrePersist
    public void generateUuid(){
        if(this.catUuid == null){
            this.catUuid = UUID.randomUUID();
        }
    }
    
}
