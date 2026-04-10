package com.example.temple_billing.service;

import com.example.temple_billing.dto.GodRequestDTO;
import com.example.temple_billing.dto.GodResponseDTO;
import com.example.temple_billing.entity.God;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.GodRepository;
import com.example.temple_billing.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GodService {

    private static final Logger logger = LoggerFactory.getLogger(GodService.class);

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

        logger.info("Creating god: {} by user: {}", dto.getGod(), userDetails.getUsername());

        God god = God.builder()
                .god(dto.getGod())
                .remarks(dto.getRemarks())
                .createdDate(LocalDateTime.now())
                .createdBy(User.builder()
                        .id(userDetails.getUserId())
                        .build())
                .build();

        godRepository.save(god);
        logger.info("God created successfully with ID: {}", god.getId());
        return mapToDTO(god);
    }

    // ======================
    // UPDATE
    // ======================
    public GodResponseDTO update(
            Long id,
            GodRequestDTO dto,
            CustomUserDetails userDetails) {

        logger.info("Updating god ID: {} by user: {}", id, userDetails.getUsername());

        God god = godRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("God not found - ID: {}", id);
                    return new RuntimeException("God not found");
                });

        god.setGod(dto.getGod());
        god.setRemarks(dto.getRemarks());
        god.setModifiedDate(LocalDateTime.now());
        god.setModifiedBy(User.builder()
                .id(userDetails.getUserId())
                .build());

        godRepository.save(god);
        logger.info("God ID: {} updated successfully", id);
        return mapToDTO(god);
    }

    // ======================
    // GET ALL (Pagination)
    // ======================
    public Page<GodResponseDTO> getAll(
            int page,
            int size,
            String sortBy) {

        logger.debug("Fetching gods - page: {}, size: {}, sortBy: {}", page, size, sortBy);

        Pageable pageable =
                PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<GodResponseDTO> result = godRepository.findAll(pageable)
                .map(this::mapToDTO);

        logger.info("Retrieved {} gods", result.getTotalElements());
        return result;
    }

    // ======================
    // DELETE
    // ======================
    public void delete(Long id) {

        logger.info("Deleting god ID: {}", id);

        God god = godRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("God not found for deletion - ID: {}", id);
                    return new RuntimeException("God not found");
                });

        godRepository.delete(god);
        logger.info("God ID: {} deleted successfully", id);
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