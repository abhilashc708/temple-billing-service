package com.example.temple_billing.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponseDTO {

    private Long id;
    private LocalDate bookingDate;

    private String vazhipadu;
    private Integer quantity;
    private Double amount;

    private String devoteeName;
    private String birthStar;

    private LocalDateTime createdDate;
}