package com.example.temple_billing.dto;

import com.example.temple_billing.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FinanceMasterRequestDTO {

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @NotBlank(message = "Title is required")
    private String title;

    private String titleMalayalam;
}