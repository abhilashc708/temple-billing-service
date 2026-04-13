package com.example.temple_billing.service;

import com.example.temple_billing.dto.IncomeRequestDTO;
import com.example.temple_billing.dto.IncomeResponseDTO;
import com.example.temple_billing.dto.IncomeSearchRequest;
import com.example.temple_billing.dto.IncomeSummaryDTO;
import com.example.temple_billing.entity.Income;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.IncomeRepository;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.utility.IncomeSpecification;
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
public class IncomeService {

    private static final Logger logger = LoggerFactory.getLogger(IncomeService.class);

    private final IncomeRepository incomeRepository;

    // ============================
    // CREATE
    // ============================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public IncomeResponseDTO create(
            IncomeRequestDTO dto,
            CustomUserDetails userDetails) {

        logger.info("Creating income for user: {} with type: {}", userDetails.getUsername(), dto.getIncomeType());
        String receiptNo = generateReceiptNo(dto.getReceiptDate());
        logger.debug("Generated receipt number: {}", receiptNo);

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
        logger.info("Income created successfully with receipt no: {} and amount: {}", receiptNo, dto.getAmount());
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

        logger.debug("Fetching income records for user: {} - page: {}, size: {}", userDetails.getUsername(), page, size);
        Pageable pageable =
                PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<Income> incomePage;

        if (isAdmin(userDetails)) {
            logger.debug("Admin user - fetching all income records");
            incomePage = incomeRepository.findAll(pageable);
        } else {
            logger.debug("Regular user - fetching own income records");
            incomePage =
                    incomeRepository.findByCreatedById(
                            userDetails.getUserId(), pageable);
        }

        logger.info("Retrieved {} income records", incomePage.getTotalElements());
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

        logger.info("Updating income ID: {} by user: {}", id, userDetails.getUsername());
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Income not found - ID: {}", id);
                    return new RuntimeException("Income not found");
                });

        // Ownership check
        if (!isAdmin(userDetails) &&
                !income.getCreatedBy().getId()
                        .equals(userDetails.getUserId())) {
            logger.warn("Access denied for user: {} to update income: {}", userDetails.getUsername(), id);
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
        logger.info("Income ID: {} updated successfully with new amount: {}", id, dto.getAmount());
        return mapToDTO(income);
    }

    // ============================
    // DELETE
    // ============================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public void delete(Long id,
                       CustomUserDetails userDetails) {

        logger.info("Deleting income ID: {} by user: {}", id, userDetails.getUsername());
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Income not found for deletion - ID: {}", id);
                    return new RuntimeException("Income not found");
                });

        if (!isAdmin(userDetails) &&
                !income.getCreatedBy().getId()
                        .equals(userDetails.getUserId())) {
            logger.warn("Access denied for user: {} to delete income: {}", userDetails.getUsername(), id);
            throw new RuntimeException("Access denied");
        }

        incomeRepository.delete(income);
        logger.info("Income ID: {} deleted successfully", id);
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

        logger.debug("Searching income records - receiptNo: {}, incomeTypes: {}, from: {}, to: {}, page: {}, size: {}", 
                receiptNo, incomeTypes, receiptFrom, receiptTo, page, size);

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

        logger.info("Income search returned {} results", result.getTotalElements());
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
                .createdBy(
                        income.getCreatedBy() != null?
                                income.getCreatedBy().getUsername() : null)
                .createdDate(income.getCreatedDate())
                .modifiedBy(
                        income.getModifiedBy() != null ?
                                income.getModifiedBy().getUsername() : null)
                .modifiedDate(income.getModifiedDate())
                .build();
    }

    public List<IncomeResponseDTO> incomeReport(IncomeSearchRequest request) {

    logger.debug("Generating income report with request: {}", request);

    Specification<Income> spec = IncomeSpecification.search(request);

    List<Income> incomeList =
            incomeRepository.findAll(spec, Sort.by("createdDate").descending());

    logger.info("Income report generated with {} records", incomeList.size());
    return incomeList.stream()
            .map(this::mapToDTO)
            .toList();
}

    public List<IncomeSummaryDTO> getSummaryReport(IncomeSearchRequest request) {

        logger.debug("Generating income summary report by range type: {}", request.getRangeType());

        Specification<Income> spec = IncomeSpecification.search(request);

        List<Income> list = incomeRepository.findAll(spec);

        logger.debug("Processing {} income records for summary", list.size());

        Map<String, Map<String, Double>> grouped = new LinkedHashMap<>();

        for (Income income : list) {

            String period = getPeriod(income.getReceiptDate(), request.getRangeType());
            String type = income.getIncomeType();

            grouped.putIfAbsent(period, new HashMap<>());

            Map<String, Double> typeMap = grouped.get(period);

            typeMap.put(type,
                    typeMap.getOrDefault(type, 0.0) + income.getAmount());
        }

        List<IncomeSummaryDTO> result = new ArrayList<>();
        List<String> sortedPeriods = new ArrayList<>(grouped.keySet());
        sortedPeriods.sort(Comparator.reverseOrder());

        for (String period : sortedPeriods) {
            for (String type : grouped.get(period).keySet()) {

                result.add(new IncomeSummaryDTO(
                        period,
                        type,
                        grouped.get(period).get(type)
                ));
            }
        }

        logger.info("Income summary report created with {} summary entries", result.size());
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