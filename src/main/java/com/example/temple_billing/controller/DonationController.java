package com.example.temple_billing.controller;

import com.example.temple_billing.dto.DonationRequestDTO;
import com.example.temple_billing.dto.DonationResponseDTO;
import com.example.temple_billing.dto.DonationSearchRequest;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    public DonationResponseDTO create(
            @RequestBody DonationRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return donationService.create(dto, userDetails);
    }

    @GetMapping
    public Page<DonationResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return donationService.getAll(page, size, sortBy, userDetails);
    }

    @PutMapping("/{id}")
    public DonationResponseDTO update(
            @PathVariable Long id,
            @RequestBody DonationRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return donationService.update(id, dto, userDetails);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        donationService.delete(id, userDetails);
    }

    @GetMapping("/search")
    public Page<DonationResponseDTO> search(
            @RequestParam(required = false) String devoteeName,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String paymentType,
            @RequestParam(required = false) LocalDate donationFrom,
            @RequestParam(required = false) LocalDate donationTo,
            @RequestParam(required = false) String donateFor,
            @RequestParam(required = false) String receiptBookNo,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return donationService.search(
                devoteeName,
                paymentStatus,
                paymentType,
                donationFrom,
                donationTo,
                donateFor,
                receiptBookNo,
                page,
                size
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/report/search")
    public Page<DonationResponseDTO> searchBookings(
            @RequestBody DonationSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return donationService.donationReport(request, page, size);
    }
}
