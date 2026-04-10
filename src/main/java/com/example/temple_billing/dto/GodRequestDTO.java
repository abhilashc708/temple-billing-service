package com.example.temple_billing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GodRequestDTO {

    @NotBlank(message = "God name is required")
    private String god;

    private String remarks;
}