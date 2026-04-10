package com.example.temple_billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class IncomeRequestDTO {

    @NotNull(message = "Receipt date is required")
    private LocalDate receiptDate;

    @NotBlank(message = "Income type is required")
    private String incomeType;

    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Mode of income is required")
    private String modeOfIncome;

    private String chequeNo;
    private LocalDate chequeDate;
    private String remarks;
}
