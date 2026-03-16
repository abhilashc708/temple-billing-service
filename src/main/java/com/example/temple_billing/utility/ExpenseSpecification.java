package com.example.temple_billing.utility;

import com.example.temple_billing.dto.ExpenseSearchRequest;
import com.example.temple_billing.entity.Expense;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseSpecification {

    public static Specification<Expense> search(
            String receiptNo,
            List<String> expenseTypes,
            LocalDate receiptFrom,
            LocalDate receiptTo,
            String remarks
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (receiptNo != null && !receiptNo.isEmpty()) {
                predicates.add(root.get("receiptNo").in(receiptNo));
            }
            // ✅ Multi-select expense type
            if (expenseTypes != null && !expenseTypes.isEmpty()) {
                predicates.add(root.get("expenseType").in(expenseTypes));
            }

            // ✅ Receipt Date Range
            if (receiptFrom != null && receiptTo != null) {
                predicates.add(cb.between(
                        root.get("receiptDate"),
                        receiptFrom,
                        receiptTo));
            } else if (receiptFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("receiptDate"),
                        receiptFrom));
            } else if (receiptTo != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("receiptDate"),
                        receiptTo));
            }
            if (remarks != null && !remarks.isEmpty()) {
                predicates.add(root.get("remarks").in(remarks));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Expense> search(ExpenseSearchRequest request) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();


            if (request.getExpenseTypes() != null && !request.getExpenseTypes().isEmpty()) {
                predicates.add(cb.equal(
                        root.get("expenseType"),
                        request.getExpenseTypes()));
            }

            // Income Date Range
            if (request.getReceiptFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("receiptDate"),
                        request.getReceiptFrom()
                ));
            }

            if (request.getReceiptTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("receiptDate"),
                        request.getReceiptTo()
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}