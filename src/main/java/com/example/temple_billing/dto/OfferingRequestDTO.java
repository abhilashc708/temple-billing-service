package com.example.temple_billing.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OfferingRequestDTO {

    @NotBlank(message = "Offering English is required")
    private String offeringEnglish;

    private String offeringMalayalam;

    @NotBlank(message = "Offering type is required")
    private String offeringType;

    @NotBlank(message = "Offering god is required")
    private String offeringGod;

    @Positive(message = "Price must be positive")
    private Double price;

    @Positive(message = "No of pooja must be positive")
    private Integer noOfPooja;

    @NotBlank(message = "Status is required")
    private String status;

    private String remarks;
}