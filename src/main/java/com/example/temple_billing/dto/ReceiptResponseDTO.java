package com.example.temple_billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiptResponseDTO {

    private Long id;
    private String receiptNumber;
    private LocalDateTime createdDate;

    private Double totalAmount;
    private String paymentStatus;
    private String paymentType;
    private String phoneNumber;

    private String createdBy;

    private List<BookingResponseDTO> bookings;
}