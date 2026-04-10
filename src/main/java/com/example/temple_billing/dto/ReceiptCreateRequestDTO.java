package com.example.temple_billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ReceiptCreateRequestDTO {

    @NotBlank(message = "Payment type is required")
    private String paymentType;

    @NotBlank(message = "Payment status is required")
    private String paymentStatus;

    private String phoneNumber;

    @NotEmpty(message = "At least one booking is required")
    private List<BookingRequestDTO> bookings;
}