package com.example.temple_billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseRequestDTO {

    @NotNull(message = "Receipt date is required")
    private LocalDate receiptDate;

    @NotBlank(message = "Expense type is required")
    private String expenseType;

    @NotBlank(message = "Paid to is required")
    private String paidTo;

    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Mode of expense is required")
    private String modeOfExpense;

    private String chequeNo;
    private LocalDate chequeDate;
    private String remarks;
}