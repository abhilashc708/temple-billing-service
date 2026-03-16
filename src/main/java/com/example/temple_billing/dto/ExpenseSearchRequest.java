package com.example.temple_billing.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseSearchRequest {
    private String expenseTypes;
    private LocalDate receiptFrom;
    private LocalDate receiptTo;
}
