package com.example.temple_billing.controller;

import com.example.temple_billing.dto.FinanceMasterRequestDTO;
import com.example.temple_billing.dto.FinanceMasterResponseDTO;
import com.example.temple_billing.entity.TransactionType;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.service.FinanceMasterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance-master")
@RequiredArgsConstructor
public class FinanceMasterController {

    private static final Logger logger = LoggerFactory.getLogger(FinanceMasterController.class);

    private final FinanceMasterService service;

    @PostMapping
    public FinanceMasterResponseDTO create(
            @RequestBody @Valid FinanceMasterRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Creating finance master record by user: {}", userDetails.getUsername());
        FinanceMasterResponseDTO response = service.create(dto, userDetails);
        logger.info("Finance master record created successfully with ID: {}", response.getId());
        return response;
    }

    @PutMapping("/{id}")
    public FinanceMasterResponseDTO update(
            @PathVariable Long id,
            @RequestBody @Valid FinanceMasterRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Updating finance master record ID: {} by user: {}", id, userDetails.getUsername());
        FinanceMasterResponseDTO response = service.update(id, dto, userDetails);
        logger.info("Finance master record ID: {} updated successfully", id);
        return response;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<FinanceMasterResponseDTO> getAll(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy) {

        logger.debug("Fetching finance master records - type: {}, page: {}, size: {}, sortBy: {}", type, page, size, sortBy);
        Page<FinanceMasterResponseDTO> result = service.getAll(type, page, size, sortBy);
        logger.info("Retrieved {} finance master records", result.getTotalElements());
        return result;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        logger.info("Deleting finance master record ID: {}", id);
        service.delete(id);
        logger.info("Finance master record ID: {} deleted successfully", id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/search")
    public Page<FinanceMasterResponseDTO> search(
            @RequestParam(required = false) String title,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        logger.debug("Searching finance master records - title: {}, page: {}, size: {}", title, page, size);
        Page<FinanceMasterResponseDTO> result = service.search(
                title,
                page,
                size
        );
        logger.info("Finance master search returned {} results", result.getTotalElements());
        return result;
    }
}
