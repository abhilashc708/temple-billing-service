package com.example.temple_billing.dto;

import com.example.temple_billing.entity.TransactionType;
import lombok.Data;

@Data
public class FinanceMasterRequestDTO {

    private TransactionType transactionType;
    private String title;
    private String titleMalayalam;
}