package com.example.temple_billing.controller;


import com.example.temple_billing.dto.*;
import com.example.temple_billing.entity.SyncLog;
import com.example.temple_billing.repository.SyncLogRepository;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.service.IncomeService;
import com.example.temple_billing.service.IncomeSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/income")
@RequiredArgsConstructor
public class IncomeController {

    private static final Logger logger = LoggerFactory.getLogger(IncomeController.class);

    private final IncomeService incomeService;
    private final IncomeSyncService incomeSyncService;
    private final SyncLogRepository syncLogRepository;

    // CREATE
    @PostMapping
    public IncomeResponseDTO create(
            @RequestBody @Valid IncomeRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Creating income from user: {}", userDetails.getUsername());
        IncomeResponseDTO response = incomeService.create(dto, userDetails);
        logger.info("Income created successfully with ID: {}", response.getId());
        return response;
    }

    // GET ALL
    @GetMapping
    public Page<IncomeResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.debug("Fetching income records - page: {}, size: {}", page, size);
        Page<IncomeResponseDTO> result = incomeService.getAll(page, size, userDetails);
        logger.info("Retrieved {} income records", result.getTotalElements());
        return result;
    }

    // UPDATE
    @PutMapping("/{id}")
    public IncomeResponseDTO update(
            @PathVariable Long id,
            @RequestBody @Valid IncomeRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Updating income ID: {} by user: {}", id, userDetails.getUsername());
        IncomeResponseDTO response = incomeService.update(id, dto, userDetails);
        logger.info("Income ID: {} updated successfully", id);
        return response;
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Deleting income ID: {} by user: {}", id, userDetails.getUsername());
        incomeService.delete(id, userDetails);
        logger.info("Income ID: {} deleted successfully", id);
    }

    @GetMapping("/search")
    public Page<IncomeResponseDTO> search(

            @RequestParam(required = false) List<String> incomeType,
            @RequestParam(required = false) String receiptNo,
            @RequestParam(required = false) LocalDate receiptFrom,
            @RequestParam(required = false) LocalDate receiptTo,
            @RequestParam(required = false) String remarks,


            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        logger.debug("Searching income - receiptNo: {}, incomeType: {}, from: {}, to: {}", receiptNo, incomeType, receiptFrom, receiptTo);
        Page<IncomeResponseDTO> result = incomeService.search(
                receiptNo,
                incomeType,
                receiptFrom,
                receiptTo,
                remarks,
                page,
                size
        );
        logger.info("Income search returned {} results", result.getTotalElements());
        return result;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/report/search")
    public List<IncomeResponseDTO> searchIncomes(
            @RequestBody IncomeSearchRequest request) {

        logger.debug("Generating income report");
        List<IncomeResponseDTO> result = incomeService.incomeReport(request);
        logger.info("Income report generated with {} records", result.size());
        return result;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/sync-income")
    public ResponseEntity<?> syncIncome() {
        logger.info("Initiating income sync");
        try {
            String message = incomeSyncService.syncIncome();
            logger.info("Income sync completed successfully: {}", message);
            return ResponseEntity.ok(
                    Map.of("message", message)
            );

        } catch (Exception e) {
            logger.error("Income sync failed: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(500)
                    .body(Map.of("message", "Sync failed: " + e.getMessage()));
        }
    }

    @GetMapping("/last-sync-date")
    public Map<String, Object> getLastSyncDate() {

        logger.debug("Retrieving last sync date");
        LocalDate lastSyncDate = syncLogRepository.findTopByOrderByIdDesc()
                .map(SyncLog::getLastSyncedDate)
                .orElse(null);

        logger.debug("Last sync date: {}", lastSyncDate);
        Map<String, Object> response = new HashMap<>();
        response.put("lastSyncDate", lastSyncDate);

        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/report/summary")
    public List<IncomeSummaryDTO> getSummaryReport(
            @RequestBody IncomeSearchRequest request) {

        logger.debug("Generating income summary report");
        List<IncomeSummaryDTO> result = incomeService.getSummaryReport(request);
        logger.info("Income summary report generated with {} entries", result.size());
        return result;
    }
}