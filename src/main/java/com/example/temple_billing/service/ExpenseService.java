package com.example.temple_billing.service;

import com.example.temple_billing.dto.ExpenseRequestDTO;
import com.example.temple_billing.dto.ExpenseResponseDTO;
import com.example.temple_billing.dto.ExpenseSearchRequest;
import com.example.temple_billing.entity.Expense;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.ExpenseRepository;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.utility.ExpenseSpecification;
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
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    // ============================
    // CREATE
    // ============================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ExpenseResponseDTO create(
            ExpenseRequestDTO dto,
            CustomUserDetails userDetails) {

        String receiptNo = generateReceiptNo(dto.getReceiptDate());

        Expense expense = Expense.builder()
                .receiptNo(receiptNo)
                .receiptDate(dto.getReceiptDate())
                .expenseType(dto.getExpenseType())
                .paidTo(dto.getPaidTo())
                .amount(dto.getAmount())
                .modeOfExpense(dto.getModeOfExpense())
                .chequeNo(dto.getChequeNo())
                .chequeDate(dto.getChequeDate())
                .remarks(dto.getRemarks())
                .createdDate(LocalDateTime.now())
                .createdBy(User.builder()
                        .id(userDetails.getUserId())
                        .build())
                .build();

        expenseRepository.save(expense);

        return mapToDTO(expense);
    }

    // ============================
    // GET ALL
    // ============================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<ExpenseResponseDTO> getAll(
            int page,
            int size,
            CustomUserDetails userDetails) {

        Pageable pageable =
                PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<Expense> expensePage;

        if (isAdmin(userDetails)) {
            expensePage = expenseRepository.findAll(pageable);
        } else {
            expensePage =
                    expenseRepository.findByCreatedById(
                            userDetails.getUserId(), pageable);
        }

        return expensePage.map(this::mapToDTO);
    }

    // ============================
    // UPDATE
    // ============================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ExpenseResponseDTO update(
            Long id,
            ExpenseRequestDTO dto,
            CustomUserDetails userDetails) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!isAdmin(userDetails) &&
                !expense.getCreatedBy().getId()
                        .equals(userDetails.getUserId())) {
            throw new RuntimeException("Access denied");
        }

        expense.setReceiptDate(dto.getReceiptDate());
        expense.setExpenseType(dto.getExpenseType());
        expense.setPaidTo(dto.getPaidTo());
        expense.setAmount(dto.getAmount());
        expense.setModeOfExpense(dto.getModeOfExpense());
        expense.setChequeNo(dto.getChequeNo());
        expense.setChequeDate(dto.getChequeDate());
        expense.setRemarks(dto.getRemarks());
        expense.setModifiedDate(LocalDateTime.now());
        expense.setModifiedBy(User.builder()
                .id(userDetails.getUserId())
                .build());

        expenseRepository.save(expense);

        return mapToDTO(expense);
    }

    // ============================
    // DELETE
    // ============================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public void delete(Long id,
                       CustomUserDetails userDetails) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!isAdmin(userDetails) &&
                !expense.getCreatedBy().getId()
                        .equals(userDetails.getUserId())) {
            throw new RuntimeException("Access denied");
        }

        expenseRepository.delete(expense);
    }

    // ============================
    // RECEIPT GENERATOR
    // ============================
    private String generateReceiptNo(LocalDate receiptDate) {

        long count =
                expenseRepository.countByReceiptDate(receiptDate);

        long nextNumber = count + 1;

        String formattedNumber =
                String.format("%02d", nextNumber);

        String formattedDate =
                receiptDate.format(
                        DateTimeFormatter.ofPattern("ddMMyyyy"));

        return "P-" + formattedNumber + "/" + formattedDate;
    }

    private boolean isAdmin(CustomUserDetails userDetails) {
        return userDetails.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority()
                        .equals("ROLE_ADMIN"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<ExpenseResponseDTO> search(
            String receiptNo,
            List<String> expenseTypes,
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

        Specification<Expense> spec =
                ExpenseSpecification.search(
                        receiptNo,
                        expenseTypes,
                        receiptFrom,
                        receiptTo,
                        remarks);

        Page<Expense> result =
                expenseRepository.findAll(spec, pageable);

        return result.map(this::mapToDTO);
    }

    private ExpenseResponseDTO mapToDTO(Expense expense) {

        return ExpenseResponseDTO.builder()
                .id(expense.getId())
                .receiptNo(expense.getReceiptNo())
                .receiptDate(expense.getReceiptDate())
                .expenseType(expense.getExpenseType())
                .paidTo(expense.getPaidTo())
                .amount(expense.getAmount())
                .modeOfExpense(expense.getModeOfExpense())
                .chequeNo(expense.getChequeNo())
                .chequeDate(expense.getChequeDate())
                .remarks(expense.getRemarks())
                .createdBy(expense.getCreatedBy().getUsername())
                .createdDate(expense.getCreatedDate())
                .modifiedBy(
                        expense.getModifiedBy() != null ?
                                expense.getModifiedBy().getUsername() : null)
                .modifiedDate(expense.getModifiedDate())
                .build();
    }

    public List<ExpenseResponseDTO> expenseReport(ExpenseSearchRequest request) {

        Specification<Expense> spec = ExpenseSpecification.search(request);

        List<Expense> expenseSearchList =
                expenseRepository.findAll(spec, Sort.by("createdDate").descending());

        return expenseSearchList.stream()
                .map(this::mapToDTO)
                .toList();
    }
}