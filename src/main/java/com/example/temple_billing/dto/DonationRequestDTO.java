package com.example.temple_billing.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DonationRequestDTO {

    private LocalDate donationDate;
    private String paymentType;
    private String paymentStatus;
    private String receiptBookNo;
    private String receiptNo;
    private String devoteeName;
    private String address;
    private String phoneNo;
    private Double amount;
    private String donateFor;
    private String remarks;
}