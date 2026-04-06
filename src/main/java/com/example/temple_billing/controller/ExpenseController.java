package com.example.temple_billing.controller;

import com.example.temple_billing.dto.ExpenseRequestDTO;
import com.example.temple_billing.dto.ExpenseResponseDTO;
import com.example.temple_billing.dto.ExpenseSearchRequest;
import com.example.temple_billing.dto.ExpenseSummaryDTO;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expense")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ExpenseResponseDTO create(
            @RequestBody ExpenseRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return expenseService.create(dto, userDetails);
    }

    @GetMapping
    public Page<ExpenseResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return expenseService.getAll(page, size, userDetails);
    }

    @PutMapping("/{id}")
    public ExpenseResponseDTO update(
            @PathVariable Long id,
            @RequestBody ExpenseRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return expenseService.update(id, dto, userDetails);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        expenseService.delete(id, userDetails);
    }


    @GetMapping("/search")
    public Page<ExpenseResponseDTO> search(
            @RequestParam(required = false) String receiptNo,
            @RequestParam(required = false) List<String> expenseType,
            @RequestParam(required = false) LocalDate receiptFrom,
            @RequestParam(required = false) LocalDate receiptTo,
            @RequestParam(required = false) String remarks,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return expenseService.search(
                receiptNo,
                expenseType,
                receiptFrom,
                receiptTo,
                remarks,
                page,
                size
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/report/search")
    public List<ExpenseResponseDTO> searchExpenseReport(
            @RequestBody ExpenseSearchRequest request) {

        return expenseService.expenseReport(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/report/summary")
    public List<ExpenseSummaryDTO> getExpenseSummaryReport(
            @RequestBody ExpenseSearchRequest request) {

        return expenseService.getSummaryReport(request);
    }
}