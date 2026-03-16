package com.example.temple_billing.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class IncomeSearchRequest {
    private String incomeTypes;
    private LocalDate receiptFrom;
    private LocalDate receiptTo;
}
