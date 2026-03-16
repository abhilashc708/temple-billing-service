package com.example.temple_billing.service;

import com.example.temple_billing.dto.GodRequestDTO;
import com.example.temple_billing.dto.GodResponseDTO;
import com.example.temple_billing.entity.God;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.GodRepository;
import com.example.temple_billing.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GodService {

    private final GodRepository godRepository;

    public GodService(GodRepository godRepository) {
        this.godRepository = godRepository;
    }

    // ======================
    // CREATE
    // ======================
    public GodResponseDTO create(
            GodRequestDTO dto,
            CustomUserDetails userDetails) {

        God god = God.builder()
                .god(dto.getGod())
                .remarks(dto.getRemarks())
                .createdDate(LocalDateTime.now())
                .createdBy(User.builder()
                        .id(userDetails.getUserId())
                        .build())
                .build();

        godRepository.save(god);

        return mapToDTO(god);
    }

    // ======================
    // UPDATE
    // ======================
    public GodResponseDTO update(
            Long id,
            GodRequestDTO dto,
            CustomUserDetails userDetails) {

        God god = godRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("God not found"));

        god.setGod(dto.getGod());
        god.setRemarks(dto.getRemarks());
        god.setModifiedDate(LocalDateTime.now());
        god.setModifiedBy(User.builder()
                .id(userDetails.getUserId())
                .build());

        godRepository.save(god);

        return mapToDTO(god);
    }

    // ======================
    // GET ALL (Pagination)
    // ======================
    public Page<GodResponseDTO> getAll(
            int page,
            int size,
            String sortBy) {

        Pageable pageable =
                PageRequest.of(page, size, Sort.by(sortBy).descending());

        return godRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    // ======================
    // DELETE
    // ======================
    public void delete(Long id) {

        God god = godRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("God not found"));

        godRepository.delete(god);
    }

    // ======================
    // MAP TO DTO
    // ======================
    private GodResponseDTO mapToDTO(God god) {

        return GodResponseDTO.builder()
                .id(god.getId())
                .god(god.getGod())
                .remarks(god.getRemarks())
                .createdBy(god.getCreatedBy().getUsername())
                .createdDate(god.getCreatedDate())
                .modifiedBy(
                        god.getModifiedBy() != null ?
                                god.getModifiedBy().getUsername() : null)
                .modifiedDate(god.getModifiedDate())
                .build();
    }
}