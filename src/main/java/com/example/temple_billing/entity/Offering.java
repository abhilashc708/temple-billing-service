package com.example.temple_billing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "offerings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Offering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String offeringEnglish;

    private String offeringMalayalam;

    private String offeringType;

    private String offeringGod;

    private Double price;

    private Integer noOfPooja;

    private String status;

    private String remarks;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    // 🔥 Created By (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // 🔥 Modified By (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modified_by")
    private User modifiedBy;
}
