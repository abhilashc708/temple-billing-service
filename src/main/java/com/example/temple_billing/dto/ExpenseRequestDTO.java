package com.example.temple_billing.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseRequestDTO {

    private LocalDate receiptDate;
    private String expenseType;
    private String paidTo;
    private Double amount;
    private String modeOfExpense;
    private String chequeNo;
    private LocalDate chequeDate;
    private String remarks;
}