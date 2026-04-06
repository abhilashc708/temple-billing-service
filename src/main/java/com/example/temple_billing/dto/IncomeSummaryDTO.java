package com.example.temple_billing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeSummaryDTO {

    private String period;
    private String incomeType;
    private Double amount;
}