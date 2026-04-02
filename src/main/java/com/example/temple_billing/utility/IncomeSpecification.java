package com.example.temple_billing.utility;

import com.example.temple_billing.dto.IncomeSearchRequest;
import com.example.temple_billing.entity.Income;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IncomeSpecification {

    public static Specification<Income> search(
            List<String> incomeTypes,
            String receiptNo,
            LocalDate receiptFrom,
            LocalDate receiptTo,
            String remarks
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // ✅ Multi-select income type
            if (incomeTypes != null && !incomeTypes.isEmpty()) {
                predicates.add(root.get("incomeType").in(incomeTypes));
            }

            if (receiptNo != null && !receiptNo.isEmpty()) {
                predicates.add(root.get("receiptNo").in(receiptNo));
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

    public static Specification<Income> search(IncomeSearchRequest request) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();


//            if (request.getIncomeTypes() != null && !request.getIncomeTypes().isEmpty()) {
//                predicates.add(cb.equal(
//                        root.get("incomeType"),
//                        request.getIncomeTypes()));
//            }
            if (request.getIncomeTypes() != null && !request.getIncomeTypes().isEmpty()) {
                predicates.add(root.get("incomeType").in(request.getIncomeTypes()));
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