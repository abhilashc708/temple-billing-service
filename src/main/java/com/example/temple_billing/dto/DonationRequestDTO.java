package com.example.temple_billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DonationRequestDTO {

    @NotNull(message = "Donation date is required")
    private LocalDate donationDate;

    @NotBlank(message = "Payment type is required")
    private String paymentType;

    @NotBlank(message = "Payment status is required")
    private String paymentStatus;

    private String receiptBookNo;

    @NotBlank(message = "Receipt number is required")
    private String receiptNo;

    @NotBlank(message = "Devotee name is required")
    private String devoteeName;

    private String address;
    private String phoneNo;

    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Donate for is required")
    private String donateFor;

    private String remarks;
}