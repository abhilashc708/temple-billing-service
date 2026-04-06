package com.example.temple_billing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseSummaryDTO {

    private String period;
    private String expenseType;
    private Double amount;
}