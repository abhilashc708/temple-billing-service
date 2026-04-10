package com.example.temple_billing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReceiptUpdateDTO {

    @NotBlank(message = "Payment type is required")
    private String paymentType;

    @NotBlank(message = "Payment status is required")
    private String paymentStatus;

    private String phoneNumber;
}