package com.example.temple_billing.controller;


import com.example.temple_billing.dto.IncomeRequestDTO;
import com.example.temple_billing.dto.IncomeResponseDTO;
import com.example.temple_billing.dto.IncomeSearchRequest;
import com.example.temple_billing.dto.LoginResponse;
import com.example.temple_billing.entity.SyncLog;
import com.example.temple_billing.repository.SyncLogRepository;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.service.IncomeService;
import com.example.temple_billing.service.IncomeSyncService;
import lombok.RequiredArgsConstructor;
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

    private final IncomeService incomeService;
    private final IncomeSyncService incomeSyncService;
    private final SyncLogRepository syncLogRepository;

    // CREATE
    @PostMapping
    public IncomeResponseDTO create(
            @RequestBody IncomeRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return incomeService.create(dto, userDetails);
    }

    // GET ALL
    @GetMapping
    public Page<IncomeResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return incomeService.getAll(page, size, userDetails);
    }

    // UPDATE
    @PutMapping("/{id}")
    public IncomeResponseDTO update(
            @PathVariable Long id,
            @RequestBody IncomeRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return incomeService.update(id, dto, userDetails);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        incomeService.delete(id, userDetails);
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

        return incomeService.search(
                receiptNo,
                incomeType,
                receiptFrom,
                receiptTo,
                remarks,
                page,
                size
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/report/search")
    public List<IncomeResponseDTO> searchIncomes(
            @RequestBody IncomeSearchRequest request) {

        return incomeService.incomeReport(request);
    }

@PreAuthorize("hasAnyRole('ADMIN','USER')")
@PostMapping("/sync-income")
public ResponseEntity<?> syncIncome() {
    try {
        String message = incomeSyncService.syncIncome();

        return ResponseEntity.ok(
                Map.of("message", message)
        );

    } catch (Exception e) {
        return ResponseEntity
                .status(500)
                .body(Map.of("message", "Sync failed: " + e.getMessage()));
    }
}
@GetMapping("/last-sync-date")
public Map<String, Object> getLastSyncDate() {

    LocalDate lastSyncDate = syncLogRepository.findTopByOrderByIdDesc()
            .map(SyncLog::getLastSyncedDate)
            .orElse(null);

    Map<String, Object> response = new HashMap<>();
    response.put("lastSyncDate", lastSyncDate);

    return response;
}
}