package com.example.temple_billing.service;

import com.example.temple_billing.dto.IncomeRequestDTO;
import com.example.temple_billing.dto.IncomeResponseDTO;
import com.example.temple_billing.dto.IncomeSearchRequest;
import com.example.temple_billing.entity.Income;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.IncomeRepository;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.utility.IncomeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;

    // ============================
    // CREATE
    // ============================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public IncomeResponseDTO create(
            IncomeRequestDTO dto,
            CustomUserDetails userDetails) {

        String receiptNo = generateReceiptNo(dto.getReceiptDate());

        Income income = Income.builder()
                .receiptNo(receiptNo)
                .receiptDate(dto.getReceiptDate())
                .incomeType(dto.getIncomeType())
                .amount(dto.getAmount())
                .modeOfIncome(dto.getModeOfIncome())
                .chequeNo(dto.getChequeNo())
                .chequeDate(dto.getChequeDate())
                .remarks(dto.getRemarks())
                .createdDate(LocalDateTime.now())
                .createdBy(User.builder()
                        .id(userDetails.getUserId())
                        .build())
                .build();

        incomeRepository.save(income);

        return mapToDTO(income);
    }

    // ============================
    // GET ALL (ADMIN: all / USER: own)
    // ============================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<IncomeResponseDTO> getAll(
            int page,
            int size,
            CustomUserDetails userDetails) {

        Pageable pageable =
                PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<Income> incomePage;

        if (isAdmin(userDetails)) {
            incomePage = incomeRepository.findAll(pageable);
        } else {
            incomePage =
                    incomeRepository.findByCreatedById(
                            userDetails.getUserId(), pageable);
        }

        return incomePage.map(this::mapToDTO);
    }

    // ============================
    // UPDATE
    // ============================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public IncomeResponseDTO update(
            Long id,
            IncomeRequestDTO dto,
            CustomUserDetails userDetails) {

        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        // Ownership check
        if (!isAdmin(userDetails) &&
                !income.getCreatedBy().getId()
                        .equals(userDetails.getUserId())) {
            throw new RuntimeException("Access denied");
        }

        income.setReceiptDate(dto.getReceiptDate());
        income.setIncomeType(dto.getIncomeType());
        income.setAmount(dto.getAmount());
        income.setModeOfIncome(dto.getModeOfIncome());
        income.setChequeNo(dto.getChequeNo());
        income.setChequeDate(dto.getChequeDate());
        income.setRemarks(dto.getRemarks());
        income.setModifiedDate(LocalDateTime.now());
        income.setModifiedBy(User.builder()
                .id(userDetails.getUserId())
                .build());

        incomeRepository.save(income);

        return mapToDTO(income);
    }

    // ============================
    // DELETE
    // ============================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public void delete(Long id,
                       CustomUserDetails userDetails) {

        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        if (!isAdmin(userDetails) &&
                !income.getCreatedBy().getId()
                        .equals(userDetails.getUserId())) {
            throw new RuntimeException("Access denied");
        }

        incomeRepository.delete(income);
    }

    // ============================
    // RECEIPT GENERATOR (Daily Reset)
    // ============================
    private String generateReceiptNo(LocalDate receiptDate) {

        long count =
                incomeRepository.countByReceiptDate(receiptDate);

        long nextNumber = count + 1;

        String formattedNumber =
                String.format("%02d", nextNumber);

        String formattedDate =
                receiptDate.format(
                        DateTimeFormatter.ofPattern("ddMMyyyy"));

        return "R-" + formattedNumber + "/" + formattedDate;
    }

    private boolean isAdmin(CustomUserDetails userDetails) {
        return userDetails.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority()
                        .equals("ROLE_ADMIN"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<IncomeResponseDTO> search(
            String receiptNo,
            List<String> incomeTypes,
            LocalDate receiptFrom,
            LocalDate receiptTo,
            String remarks,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("receiptDate").descending());

        Specification<Income> spec =
                IncomeSpecification.search(
                        incomeTypes,
                        receiptNo,
                        receiptFrom,
                        receiptTo,
                        remarks);

        Page<Income> result =
                incomeRepository.findAll(spec, pageable);

        return result.map(this::mapToDTO);
    }

    private IncomeResponseDTO mapToDTO(Income income) {

        return IncomeResponseDTO.builder()
                .id(income.getId())
                .receiptNo(income.getReceiptNo())
                .receiptDate(income.getReceiptDate())
                .incomeType(income.getIncomeType())
                .amount(income.getAmount())
                .modeOfIncome(income.getModeOfIncome())
                .chequeNo(income.getChequeNo())
                .chequeDate(income.getChequeDate())
                .remarks(income.getRemarks())
                .createdBy(income.getCreatedBy().getUsername())
                .createdDate(income.getCreatedDate())
                .modifiedBy(
                        income.getModifiedBy() != null ?
                                income.getModifiedBy().getUsername() : null)
                .modifiedDate(income.getModifiedDate())
                .build();
    }

    public Page<IncomeResponseDTO> incomeReport(IncomeSearchRequest request,
                                                int page,
                                                int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdDate").descending());

        Specification<Income> spec = IncomeSpecification.search(request);

        Page<Income> incomeSearchList =
                incomeRepository.findAll(spec, pageable);

        return incomeSearchList.map(this::mapToDTO);
    }
}