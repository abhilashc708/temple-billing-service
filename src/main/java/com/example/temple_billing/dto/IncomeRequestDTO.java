package com.example.temple_billing.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class IncomeRequestDTO {

    private LocalDate receiptDate;
    private String incomeType;
    private Double amount;
    private String modeOfIncome;
    private String chequeNo;
    private LocalDate chequeDate;
    private String remarks;
}
