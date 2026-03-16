package com.example.temple_billing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ExpenseResponseDTO {

    private Long id;
    private String receiptNo;
    private LocalDate receiptDate;
    private String expenseType;
    private String paidTo;
    private Double amount;
    private String modeOfExpense;
    private String chequeNo;
    private LocalDate chequeDate;
    private String remarks;
    private String createdBy;
    private LocalDateTime createdDate;
    private String modifiedBy;
    private LocalDateTime modifiedDate;
}