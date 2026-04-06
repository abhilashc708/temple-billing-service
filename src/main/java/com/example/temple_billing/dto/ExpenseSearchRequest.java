package com.example.temple_billing.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ExpenseSearchRequest {
//    private String expenseTypes;
private List<String> expenseTypes;
    private LocalDate receiptFrom;
    private LocalDate receiptTo;
    private String rangeType;
}
