package com.example.temple_billing.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDTO {

    private LocalDate bookingDate;
    private String paymentType;
    private String paymentStatus;
    private String phoneNumber;
    private String vazhipadu;
    private Integer quantity;
    private Double amount;
    private String devoteeName;
    private String birthStar;
}