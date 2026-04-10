package com.example.temple_billing.controller;

import com.example.temple_billing.dto.EventRequestDTO;
import com.example.temple_billing.dto.EventResponseDTO;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;

    // CREATE
    @PostMapping
    public EventResponseDTO create(
            @RequestBody @Valid EventRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Creating event: {} by user: {}", dto.getEventName(), userDetails.getUsername());
        EventResponseDTO response = eventService.create(dto, userDetails);
        logger.info("Event created successfully with ID: {}", response.getId());
        return response;
    }

    // UPDATE
    @PutMapping("/{id}")
    public EventResponseDTO update(
            @PathVariable Long id,
            @RequestBody @Valid EventRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Updating event ID: {} by user: {}", id, userDetails.getUsername());
        EventResponseDTO response = eventService.update(id, dto, userDetails);
        logger.info("Event ID: {} updated successfully", id);
        return response;
    }

    // GET ALL
    @GetMapping
    public Page<EventResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy) {

        logger.debug("Fetching events - page: {}, size: {}, sortBy: {}", page, size, sortBy);
        Page<EventResponseDTO> result = eventService.getAll(page, size, sortBy);
        logger.info("Retrieved {} events", result.getTotalElements());
        return result;
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        logger.info("Deleting event ID: {}", id);
        eventService.delete(id);
        logger.info("Event ID: {} deleted successfully", id);
    }
}