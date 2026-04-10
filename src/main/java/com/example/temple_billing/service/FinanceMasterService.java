package com.example.temple_billing.service;

import com.example.temple_billing.dto.FinanceMasterRequestDTO;
import com.example.temple_billing.dto.FinanceMasterResponseDTO;
import com.example.temple_billing.entity.FinanceMaster;
import com.example.temple_billing.entity.TransactionType;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.FinanceMasterRepository;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.utility.FinanceSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FinanceMasterService {

    private static final Logger logger = LoggerFactory.getLogger(FinanceMasterService.class);

    private final FinanceMasterRepository repository;

    // ======================
    // CREATE (ADMIN ONLY)
    // ======================
    @PreAuthorize("hasRole('ADMIN')")
    public FinanceMasterResponseDTO create(
            FinanceMasterRequestDTO dto,
            CustomUserDetails userDetails) {

        logger.info("Creating finance master record: {} (Type: {}) by user: {}", 
                dto.getTitle(), dto.getTransactionType(), userDetails.getUsername());

        FinanceMaster entity = FinanceMaster.builder()
                .transactionType(dto.getTransactionType())
                .title(dto.getTitle())
                .titleMalayalam(dto.getTitleMalayalam())
                .createdDate(LocalDateTime.now())
                .createdBy(User.builder()
                        .id(userDetails.getUserId())
                        .build())
                .build();

        repository.save(entity);
        logger.info("Finance master record created successfully with ID: {}", entity.getId());
        return mapToDTO(entity);
    }

    // ======================
    // UPDATE (ADMIN ONLY)
    // ======================
    @PreAuthorize("hasRole('ADMIN')")
    public FinanceMasterResponseDTO update(
            Long id,
            FinanceMasterRequestDTO dto,
            CustomUserDetails userDetails) {

        logger.info("Updating finance master record ID: {} by user: {}", id, userDetails.getUsername());

        FinanceMaster entity = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Finance master record not found - ID: {}", id);
                    return new RuntimeException("Record not found");
                });

        entity.setTransactionType(dto.getTransactionType());
        entity.setTitle(dto.getTitle());
        entity.setTitleMalayalam(dto.getTitleMalayalam());
        entity.setModifiedDate(LocalDateTime.now());
        entity.setModifiedBy(User.builder()
                .id(userDetails.getUserId())
                .build());

        repository.save(entity);
        logger.info("Finance master record ID: {} updated successfully", id);
        return mapToDTO(entity);
    }

    public Page<FinanceMasterResponseDTO> getAll(
            TransactionType type,
            int page,
            int size,
            String sortBy) {

        logger.debug("Fetching finance master records - type: {}, page: {}, size: {}, sortBy: {}", type, page, size, sortBy);

        Pageable pageable =
                PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<FinanceMaster> pageResult;

        if (type != null) {
            logger.debug("Filtering by transaction type: {}", type);
            pageResult = repository.findByTransactionType(type, pageable);
        } else {
            pageResult = repository.findAll(pageable);
        }

        logger.info("Retrieved {} finance master records", pageResult.getTotalElements());
        return pageResult.map(this::mapToDTO);
    }

    // ======================
    // DELETE (ADMIN ONLY)
    // ======================
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {

        logger.info("Deleting finance master record ID: {}", id);

        FinanceMaster entity = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Finance master record not found for deletion - ID: {}", id);
                    return new RuntimeException("Record not found");
                });

        repository.delete(entity);
        logger.info("Finance master record ID: {} deleted successfully", id);
    }

    private FinanceMasterResponseDTO mapToDTO(FinanceMaster entity) {

        return FinanceMasterResponseDTO.builder()
                .id(entity.getId())
                .transactionType(entity.getTransactionType())
                .title(entity.getTitle())
                .titleMalayalam(entity.getTitleMalayalam())
                .createdBy(
                        entity.getCreatedBy() != null ?
                                entity.getCreatedBy().getUsername() : null)
                .createdDate(entity.getCreatedDate())
                .modifiedBy(
                        entity.getModifiedBy() != null ?
                                entity.getModifiedBy().getUsername() : null)
                .modifiedDate(entity.getModifiedDate())
                .build();
    }

    public Page<FinanceMasterResponseDTO> search(
            String title,
            int page,
            int size
    ) {

        logger.debug("Searching finance master records - title: {}, page: {}, size: {}", title, page, size);

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdDate").descending());

        var spec = FinanceSpecification.search(
                title);

        Page<FinanceMaster> financeList =
                repository.findAll(spec, pageable);

        logger.info("Finance master search returned {} results", financeList.getTotalElements());
        return financeList.map(this::mapToDTO);
    }
}