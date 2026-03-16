package com.example.temple_billing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class IncomeResponseDTO {

    private Long id;
    private String receiptNo;
    private LocalDate receiptDate;

    private String incomeType;
    private Double amount;

    private String modeOfIncome;
    private String chequeNo;
    private LocalDate chequeDate;

    private String remarks;

    private String createdBy;
    private LocalDateTime createdDate;
    private String modifiedBy;
    private LocalDateTime modifiedDate;
}