package com.example.temple_billing.controller;

import com.example.temple_billing.dto.EventRequestDTO;
import com.example.temple_billing.dto.EventResponseDTO;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // CREATE
    @PostMapping
    public EventResponseDTO create(
            @RequestBody EventRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return eventService.create(dto, userDetails);
    }

    // UPDATE
    @PutMapping("/{id}")
    public EventResponseDTO update(
            @PathVariable Long id,
            @RequestBody EventRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return eventService.update(id, dto, userDetails);
    }

    // GET ALL
    @GetMapping
    public Page<EventResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy) {

        return eventService.getAll(page, size, sortBy);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        eventService.delete(id);
    }
}