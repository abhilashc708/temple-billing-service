package com.example.temple_billing.utility;

import com.example.temple_billing.entity.FinanceMaster;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FinanceSpecification {

    public static Specification<FinanceMaster> search(
            String title
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.equal(
                        root.get("title"),
                        title));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
