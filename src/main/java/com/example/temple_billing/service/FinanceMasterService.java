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

    private final FinanceMasterRepository repository;

    // ======================
    // CREATE (ADMIN ONLY)
    // ======================
    @PreAuthorize("hasRole('ADMIN')")
    public FinanceMasterResponseDTO create(
            FinanceMasterRequestDTO dto,
            CustomUserDetails userDetails) {

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

        FinanceMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        entity.setTransactionType(dto.getTransactionType());
        entity.setTitle(dto.getTitle());
        entity.setTitleMalayalam(dto.getTitleMalayalam());
        entity.setModifiedDate(LocalDateTime.now());
        entity.setModifiedBy(User.builder()
                .id(userDetails.getUserId())
                .build());

        repository.save(entity);

        return mapToDTO(entity);
    }

    public Page<FinanceMasterResponseDTO> getAll(
            TransactionType type,
            int page,
            int size,
            String sortBy) {

        Pageable pageable =
                PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<FinanceMaster> pageResult;

        if (type != null) {
            pageResult = repository.findByTransactionType(type, pageable);
        } else {
            pageResult = repository.findAll(pageable);
        }

        return pageResult.map(this::mapToDTO);
    }

    // ======================
    // DELETE (ADMIN ONLY)
    // ======================
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {

        FinanceMaster entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        repository.delete(entity);
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

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdDate").descending());

        var spec = FinanceSpecification.search(
                title);

        Page<FinanceMaster> financeList =
                repository.findAll(spec, pageable);

        return financeList.map(this::mapToDTO);
    }
}