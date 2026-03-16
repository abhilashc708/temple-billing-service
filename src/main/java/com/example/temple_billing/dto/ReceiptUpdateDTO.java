package com.example.temple_billing.dto;

import lombok.Data;

@Data
public class ReceiptUpdateDTO {

    private String paymentType;
    private String paymentStatus;
    private String phoneNumber;
}