package com.example.temple_billing.controller;

import com.example.temple_billing.dto.OfferingRequestDTO;
import com.example.temple_billing.dto.OfferingResponseDTO;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.service.OfferingService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/offerings")
public class OfferingController {

    private final OfferingService offeringService;

    public OfferingController(OfferingService offeringService) {
        this.offeringService = offeringService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public OfferingResponseDTO create(
            @RequestBody OfferingRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return offeringService.create(dto, userDetails);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public OfferingResponseDTO update(
            @PathVariable Long id,
            @RequestBody OfferingRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return offeringService.update(id, dto, userDetails);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public Page<OfferingResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return offeringService.getAll(page, size, sortBy, userDetails);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/getAllByStatus")
    public Page<OfferingResponseDTO> getAllByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return offeringService.getAllByStatus(page, size, sortBy, userDetails);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public Page<OfferingResponseDTO> search(
            @RequestParam(required = false) String offeringEnglish,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return offeringService.search(
                offeringEnglish,
                page,
                size
        );
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        offeringService.delete(id, userDetails);
    }
}