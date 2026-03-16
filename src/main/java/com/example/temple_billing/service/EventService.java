package com.example.temple_billing.service;

import com.example.temple_billing.dto.EventRequestDTO;
import com.example.temple_billing.dto.EventResponseDTO;
import com.example.temple_billing.entity.Event;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.EventRepository;
import com.example.temple_billing.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    // ========================
    // CREATE (ADMIN ONLY)
    // ========================
    @PreAuthorize("hasRole('ADMIN')")
    public EventResponseDTO create(
            EventRequestDTO dto,
            CustomUserDetails userDetails) {

        Event event = Event.builder()
                .eventName(dto.getEventName())
                .createdDate(LocalDateTime.now())
                .createdBy(User.builder()
                        .id(userDetails.getUserId())
                        .build())
                .build();

        eventRepository.save(event);

        return mapToDTO(event);
    }

    // ========================
    // UPDATE (ADMIN ONLY)
    // ========================
    @PreAuthorize("hasRole('ADMIN')")
    public EventResponseDTO update(
            Long id,
            EventRequestDTO dto,
            CustomUserDetails userDetails) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setEventName(dto.getEventName());
        event.setModifiedDate(LocalDateTime.now());
        event.setModifiedBy(User.builder()
                .id(userDetails.getUserId())
                .build());

        eventRepository.save(event);

        return mapToDTO(event);
    }

    // ========================
    // GET ALL (ADMIN + USER)
    // ========================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<EventResponseDTO> getAll(
            int page,
            int size,
            String sortBy) {

        Pageable pageable =
                PageRequest.of(page, size, Sort.by(sortBy).descending());

        return eventRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    // ========================
    // DELETE (ADMIN ONLY)
    // ========================
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        eventRepository.delete(event);
    }

    private EventResponseDTO mapToDTO(Event event) {

        return EventResponseDTO.builder()
                .id(event.getId())
                .eventName(event.getEventName())
                .createdBy(
                        event.getCreatedBy() != null ?
                                event.getCreatedBy().getUsername() : null)
                .createdDate(event.getCreatedDate())
                .modifiedBy(
                        event.getModifiedBy() != null ?
                                event.getModifiedBy().getUsername() : null)
                .modifiedDate(event.getModifiedDate())
                .build();
    }
}
