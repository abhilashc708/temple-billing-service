package com.example.temple_billing.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReceiptCreateRequestDTO {

    private String paymentType;
    private String paymentStatus;
    private String phoneNumber;

    private List<BookingRequestDTO> bookings;
}