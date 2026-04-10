package com.example.temple_billing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventRequestDTO {
    @NotBlank(message = "Event name is required")
    private String eventName;
}