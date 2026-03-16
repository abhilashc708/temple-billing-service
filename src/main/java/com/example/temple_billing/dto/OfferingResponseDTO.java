package com.example.temple_billing.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OfferingResponseDTO {

    private Long id;
    private String offeringEnglish;
    private String offeringMalayalam;
    private String offeringType;
    private String offeringGod;
    private Double price;
    private Integer noOfPooja;
    private String status;
    private String remarks;
    private String createdBy;
    private LocalDateTime createdDate;
    private String modifiedBy;
    private LocalDateTime modifiedDate;
}