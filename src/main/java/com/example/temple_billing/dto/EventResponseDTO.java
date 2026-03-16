package com.example.temple_billing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventResponseDTO {

    private Long id;
    private String eventName;

    private String createdBy;
    private LocalDateTime createdDate;

    private String modifiedBy;
    private LocalDateTime modifiedDate;
}
