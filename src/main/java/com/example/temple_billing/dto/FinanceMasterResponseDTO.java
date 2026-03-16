package com.example.temple_billing.dto;

import com.example.temple_billing.entity.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FinanceMasterResponseDTO {

    private Long id;
    private TransactionType transactionType;
    private String title;
    private String titleMalayalam;

    private String createdBy;
    private LocalDateTime createdDate;
    private String modifiedBy;
    private LocalDateTime modifiedDate;
}