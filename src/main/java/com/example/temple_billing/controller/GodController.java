package com.example.temple_billing.controller;

import com.example.temple_billing.dto.GodRequestDTO;
import com.example.temple_billing.dto.GodResponseDTO;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.service.GodService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gods")
public class GodController {

    private final GodService godService;

    public GodController(GodService godService) {
        this.godService = godService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public GodResponseDTO create(
            @RequestBody GodRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return godService.create(dto, userDetails);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public GodResponseDTO update(
            @PathVariable Long id,
            @RequestBody GodRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return godService.update(id, dto, userDetails);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public Page<GodResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy) {

        return godService.getAll(page, size, sortBy);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        godService.delete(id);
    }
}