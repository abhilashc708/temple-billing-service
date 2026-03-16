package com.example.temple_billing.dto;


import lombok.Data;

@Data
public class OfferingRequestDTO {

    private String offeringEnglish;
    private String offeringMalayalam;
    private String offeringType;
    private String offeringGod;
    private Double price;
    private Integer noOfPooja;
    private String status;
    private String remarks;
}