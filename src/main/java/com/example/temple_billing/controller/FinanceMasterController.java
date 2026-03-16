package com.example.temple_billing.controller;

import com.example.temple_billing.dto.FinanceMasterRequestDTO;
import com.example.temple_billing.dto.FinanceMasterResponseDTO;
import com.example.temple_billing.entity.TransactionType;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.service.FinanceMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance-master")
@RequiredArgsConstructor
public class FinanceMasterController {

    private final FinanceMasterService service;

    @PostMapping
    public FinanceMasterResponseDTO create(
            @RequestBody FinanceMasterRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return service.create(dto, userDetails);
    }

    @PutMapping("/{id}")
    public FinanceMasterResponseDTO update(
            @PathVariable Long id,
            @RequestBody FinanceMasterRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return service.update(id, dto, userDetails);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<FinanceMasterResponseDTO> getAll(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy) {

        return service.getAll(type, page, size, sortBy);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/search")
    public Page<FinanceMasterResponseDTO> search(
            @RequestParam(required = false) String title,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return service.search(
                title,
                page,
                size
        );
    }
}
