package com.example.temple_billing.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DonationSearchRequest {
    private String paymentStatus;
    private String paymentType;
    private String donateFor;
    private String receiptBookNumber;
    private LocalDate donationFromDate;
    private LocalDate donationToDate;
}
