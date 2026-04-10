package com.example.temple_billing.controller;

import com.example.temple_billing.dto.ExpenseRequestDTO;
import com.example.temple_billing.dto.ExpenseResponseDTO;
import com.example.temple_billing.dto.ExpenseSearchRequest;
import com.example.temple_billing.dto.ExpenseSummaryDTO;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expense")
@RequiredArgsConstructor
public class ExpenseController {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);

    private final ExpenseService expenseService;

    @PostMapping
    public ExpenseResponseDTO create(
            @RequestBody @Valid ExpenseRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Creating expense from user: {}", userDetails.getUsername());
        ExpenseResponseDTO response = expenseService.create(dto, userDetails);
        logger.info("Expense created successfully with ID: {}", response.getId());
        return response;
    }

    @GetMapping
    public Page<ExpenseResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.debug("Fetching expenses - page: {}, size: {}", page, size);
        Page<ExpenseResponseDTO> result = expenseService.getAll(page, size, userDetails);
        logger.info("Retrieved {} expenses", result.getTotalElements());
        return result;
    }

    @PutMapping("/{id}")
    public ExpenseResponseDTO update(
            @PathVariable Long id,
            @RequestBody @Valid ExpenseRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Updating expense ID: {} by user: {}", id, userDetails.getUsername());
        ExpenseResponseDTO response = expenseService.update(id, dto, userDetails);
        logger.info("Expense ID: {} updated successfully", id);
        return response;
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Deleting expense ID: {} by user: {}", id, userDetails.getUsername());
        expenseService.delete(id, userDetails);
        logger.info("Expense ID: {} deleted successfully", id);
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

        logger.debug("Searching expenses - receiptNo: {}, expenseType: {}, from: {}, to: {}", receiptNo, expenseType, receiptFrom, receiptTo);
        Page<ExpenseResponseDTO> result = expenseService.search(
                receiptNo,
                expenseType,
                receiptFrom,
                receiptTo,
                remarks,
                page,
                size
        );
        logger.info("Expense search returned {} results", result.getTotalElements());
        return result;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/report/search")
    public List<ExpenseResponseDTO> searchExpenseReport(
            @RequestBody ExpenseSearchRequest request) {

        logger.debug("Generating expense report");
        List<ExpenseResponseDTO> result = expenseService.expenseReport(request);
        logger.info("Expense report generated with {} records", result.size());
        return result;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/report/summary")
    public List<ExpenseSummaryDTO> getExpenseSummaryReport(
            @RequestBody ExpenseSearchRequest request) {

        logger.debug("Generating expense summary report");
        List<ExpenseSummaryDTO> result = expenseService.getSummaryReport(request);
        logger.info("Expense summary report generated with {} entries", result.size());
        return result;
    }
}