package com.example.temple_billing.service;

import com.example.temple_billing.dto.ExpenseRequestDTO;
import com.example.temple_billing.dto.ExpenseResponseDTO;
import com.example.temple_billing.dto.ExpenseSearchRequest;
import com.example.temple_billing.dto.ExpenseSummaryDTO;
import com.example.temple_billing.entity.Expense;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.ExpenseRepository;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.utility.ExpenseSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.temporal.WeekFields;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);

    private final ExpenseRepository expenseRepository;

    // ============================
    // CREATE
    // ============================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ExpenseResponseDTO create(
            ExpenseRequestDTO dto,
            CustomUserDetails userDetails) {

        logger.info("Creating expense for user: {} with type: {}", userDetails.getUsername(), dto.getExpenseType());
        String receiptNo = generateReceiptNo(dto.getReceiptDate());
        logger.debug("Generated receipt number: {}", receiptNo);

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
        logger.info("Expense created successfully with receipt no: {} and amount: {}", receiptNo, dto.getAmount());
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

        logger.debug("Fetching expenses for user: {} - page: {}, size: {}", userDetails.getUsername(), page, size);
        Pageable pageable =
                PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<Expense> expensePage;

        if (isAdmin(userDetails)) {
            logger.debug("Admin user - fetching all expenses");
            expensePage = expenseRepository.findAll(pageable);
        } else {
            logger.debug("Regular user - fetching own expenses");
            expensePage =
                    expenseRepository.findByCreatedById(
                            userDetails.getUserId(), pageable);
        }
        logger.info("Retrieved {} expenses", expensePage.getTotalElements());
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

        logger.info("Updating expense ID: {} by user: {}", id, userDetails.getUsername());
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Expense not found - ID: {}", id);
                    return new RuntimeException("Expense not found");
                });

        if (!isAdmin(userDetails) &&
                !expense.getCreatedBy().getId()
                        .equals(userDetails.getUserId())) {
            logger.warn("Access denied for user: {} to update expense: {}", userDetails.getUsername(), id);
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
        logger.info("Expense ID: {} updated successfully with new amount: {}", id, dto.getAmount());
        return mapToDTO(expense);
    }

    // ============================
    // DELETE
    // ============================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public void delete(Long id,
                       CustomUserDetails userDetails) {

        logger.info("Deleting expense ID: {} by user: {}", id, userDetails.getUsername());
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Expense not found for deletion - ID: {}", id);
                    return new RuntimeException("Expense not found");
                });

        if (!isAdmin(userDetails) &&
                !expense.getCreatedBy().getId()
                        .equals(userDetails.getUserId())) {
            logger.warn("Access denied for user: {} to delete expense: {}", userDetails.getUsername(), id);
            throw new RuntimeException("Access denied");
        }

        expenseRepository.delete(expense);
        logger.info("Expense ID: {} deleted successfully", id);
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

        logger.debug("Searching expenses - receiptNo: {}, expenseTypes: {}, from: {}, to: {}, page: {}, size: {}", 
                receiptNo, expenseTypes, receiptFrom, receiptTo, page, size);

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

        logger.info("Expense search returned {} results", result.getTotalElements());
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

        logger.debug("Generating expense report with request: {}", request);

        Specification<Expense> spec = ExpenseSpecification.search(request);

        List<Expense> expenseSearchList =
                expenseRepository.findAll(spec, Sort.by("createdDate").descending());

        logger.info("Expense report generated with {} records", expenseSearchList.size());
        return expenseSearchList.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<ExpenseSummaryDTO> getSummaryReport(ExpenseSearchRequest request) {

        logger.debug("Generating expense summary report by range type: {}", request.getRangeType());

        Specification<Expense> spec = ExpenseSpecification.search(request);

        List<Expense> list = expenseRepository.findAll(spec);

        logger.debug("Processing {} expenses for summary", list.size());

        Map<String, Map<String, Double>> grouped = new LinkedHashMap<>();

        for (Expense expense : list) {

            String period = getPeriod(expense.getReceiptDate(), request.getRangeType());
            String type = expense.getExpenseType();

            grouped.putIfAbsent(period, new HashMap<>());

            Map<String, Double> typeMap = grouped.get(period);

            typeMap.put(type,
                    typeMap.getOrDefault(type, 0.0) + expense.getAmount());
        }

        List<ExpenseSummaryDTO> result = new ArrayList<>();

        for (String period : grouped.keySet()) {

            for (String type : grouped.get(period).keySet()) {

                result.add(new ExpenseSummaryDTO(
                        period,
                        type,
                        grouped.get(period).get(type)
                ));
            }
        }

        logger.info("Expense summary report created with {} summary entries", result.size());
        return result;
    }

    private String getPeriod(LocalDate date, String type) {

        switch (type) {

            case "DAY":
                return date.toString();

            case "MONTH":
                return date.getMonth() + " " + date.getYear();

            case "YEAR":
                return String.valueOf(date.getYear());

            case "WEEK":
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                int week = date.get(weekFields.weekOfWeekBasedYear());
                return "Week " + week + " - " + date.getYear();

            default:
                return date.toString();
        }
    }
}