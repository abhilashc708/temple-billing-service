package com.example.temple_billing.controller;

import com.example.temple_billing.dto.GodRequestDTO;
import com.example.temple_billing.dto.GodResponseDTO;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.service.GodService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gods")
public class GodController {

    private static final Logger logger = LoggerFactory.getLogger(GodController.class);

    private final GodService godService;

    public GodController(GodService godService) {
        this.godService = godService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public GodResponseDTO create(
            @RequestBody @Valid GodRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Creating god by user: {}", userDetails.getUsername());
        GodResponseDTO response = godService.create(dto, userDetails);
        logger.info("God created successfully with ID: {}", response.getId());
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public GodResponseDTO update(
            @PathVariable Long id,
            @RequestBody @Valid GodRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Updating god ID: {} by user: {}", id, userDetails.getUsername());
        GodResponseDTO response = godService.update(id, dto, userDetails);
        logger.info("God ID: {} updated successfully", id);
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public Page<GodResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy) {

        logger.debug("Fetching gods - page: {}, size: {}, sortBy: {}", page, size, sortBy);
        Page<GodResponseDTO> result = godService.getAll(page, size, sortBy);
        logger.info("Retrieved {} gods", result.getTotalElements());
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        logger.info("Deleting god ID: {}", id);
        godService.delete(id);
        logger.info("God ID: {} deleted successfully", id);
    }
}