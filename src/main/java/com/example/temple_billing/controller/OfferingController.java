package com.example.temple_billing.controller;

import com.example.temple_billing.dto.OfferingRequestDTO;
import com.example.temple_billing.dto.OfferingResponseDTO;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.service.OfferingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/offerings")
public class OfferingController {

    private static final Logger logger = LoggerFactory.getLogger(OfferingController.class);

    private final OfferingService offeringService;

    public OfferingController(OfferingService offeringService) {
        this.offeringService = offeringService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public OfferingResponseDTO create(
            @RequestBody @Valid OfferingRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Creating offering by user: {}", userDetails.getUsername());
        OfferingResponseDTO response = offeringService.create(dto, userDetails);
        logger.info("Offering created successfully with ID: {}", response.getId());
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public OfferingResponseDTO update(
            @PathVariable Long id,
            @RequestBody @Valid OfferingRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Updating offering ID: {} by user: {}", id, userDetails.getUsername());
        OfferingResponseDTO response = offeringService.update(id, dto, userDetails);
        logger.info("Offering ID: {} updated successfully", id);
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public Page<OfferingResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.debug("Fetching all offerings - page: {}, size: {}, sortBy: {}", page, size, sortBy);
        Page<OfferingResponseDTO> result = offeringService.getAll(page, size, sortBy, userDetails);
        logger.info("Retrieved {} offerings", result.getTotalElements());
        return result;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/getAllByStatus")
    public Page<OfferingResponseDTO> getAllByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.debug("Fetching active offerings - page: {}, size: {}, sortBy: {}", page, size, sortBy);
        Page<OfferingResponseDTO> result = offeringService.getAllByStatus(page, size, sortBy, userDetails);
        logger.info("Retrieved {} active offerings", result.getTotalElements());
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public Page<OfferingResponseDTO> search(
            @RequestParam(required = false) String offeringEnglish,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        logger.debug("Searching offerings - name: {}, page: {}, size: {}", offeringEnglish, page, size);
        Page<OfferingResponseDTO> result = offeringService.search(
                offeringEnglish,
                page,
                size
        );
        logger.info("Offering search returned {} results", result.getTotalElements());
        return result;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Deleting offering ID: {} by user: {}", id, userDetails.getUsername());
        offeringService.delete(id, userDetails);
        logger.info("Offering ID: {} deleted successfully", id);
    }
}