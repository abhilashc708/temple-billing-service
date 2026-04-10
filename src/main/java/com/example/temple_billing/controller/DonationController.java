package com.example.temple_billing.controller;

import com.example.temple_billing.dto.DonationRequestDTO;
import com.example.temple_billing.dto.DonationResponseDTO;
import com.example.temple_billing.dto.DonationSearchRequest;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private static final Logger logger = LoggerFactory.getLogger(DonationController.class);

    private final DonationService donationService;

    @PostMapping
    public DonationResponseDTO create(
            @RequestBody @Valid DonationRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Creating donation from user: {}", userDetails.getUsername());
        DonationResponseDTO response = donationService.create(dto, userDetails);
        logger.info("Donation created successfully with ID: {}", response.getId());
        return response;
    }

    @GetMapping
    public Page<DonationResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.debug("Fetching donations - page: {}, size: {}, sortBy: {}", page, size, sortBy);
        Page<DonationResponseDTO> result = donationService.getAll(page, size, sortBy, userDetails);
        logger.info("Retrieved {} donations", result.getTotalElements());
        return result;
    }

    @PutMapping("/{id}")
    public DonationResponseDTO update(
            @PathVariable Long id,
            @RequestBody @Valid DonationRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Updating donation ID: {} by user: {}", id, userDetails.getUsername());
        DonationResponseDTO response = donationService.update(id, dto, userDetails);
        logger.info("Donation ID: {} updated successfully", id);
        return response;
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Deleting donation ID: {} by user: {}", id, userDetails.getUsername());
        donationService.delete(id, userDetails);
        logger.info("Donation ID: {} deleted successfully", id);
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

        logger.debug("Searching donations - devoteeName: {}, paymentStatus: {}, paymentType: {}, from: {}, to: {}", 
                devoteeName, paymentStatus, paymentType, donationFrom, donationTo);

        Page<DonationResponseDTO> result = donationService.search(
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
        logger.info("Donation search returned {} results", result.getTotalElements());
        return result;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/report/search")
    public List<DonationResponseDTO> searchBookings(
            @RequestBody DonationSearchRequest request) {

        logger.debug("Generating donation report");
        List<DonationResponseDTO> result = donationService.donationReport(request);
        logger.info("Donation report generated with {} records", result.size());
        return result;
    }
}
