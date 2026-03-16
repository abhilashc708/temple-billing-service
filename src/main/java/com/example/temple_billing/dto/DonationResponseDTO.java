package com.example.temple_billing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class DonationResponseDTO {

    private Long id;
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

    private String createdBy;
    private LocalDateTime createdDate;
    private String modifiedBy;
    private LocalDateTime modifiedDate;
}
