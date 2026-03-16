package com.example.temple_billing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingReportDTO {

    private String receiptNumber;
    private LocalDateTime createdDate;
    private String paymentStatus;
    private String paymentType;

    private String devoteeName;
    private String birthStar;
    private String vazhipadu;
    private LocalDate bookingDate;

    private Integer quantity;
    private Double amount;
}
