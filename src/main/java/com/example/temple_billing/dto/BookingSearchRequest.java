package com.example.temple_billing.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingSearchRequest {
    private String vazhipadu;
    private String birthStar;

    private LocalDate vazhipaduFromDate;
    private LocalDate vazhipaduToDate;

    private LocalDate bookingFromDate;
    private LocalDate bookingToDate;

    private String paymentStatus;
    private String paymentType;
}
