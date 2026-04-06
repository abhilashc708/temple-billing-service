package com.example.temple_billing.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class IncomeSearchRequest {
    //private String incomeTypes;
    private List<String> incomeTypes;
    private LocalDate receiptFrom;
    private LocalDate receiptTo;
    private String rangeType;
}
