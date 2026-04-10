package com.example.temple_billing.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDTO {

    @NotNull(message = "Booking date is required")
    private LocalDate bookingDate;

    @NotBlank(message = "Payment type is required")
    private String paymentType;

    @NotBlank(message = "Payment status is required")
    private String paymentStatus;

    private String phoneNumber;

    @NotBlank(message = "Vazhipadu is required")
    private String vazhipadu;

    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Devotee name is required")
    private String devoteeName;

    private String birthStar;
}