package com.example.temple_billing.utility;

import com.example.temple_billing.entity.Offering;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OfferingSpecification {

    public static Specification<Offering> search(
            String offeringEnglish
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (offeringEnglish != null && !offeringEnglish.isEmpty()) {
                predicates.add(cb.equal(
                        root.get("offeringEnglish"),
                        offeringEnglish));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
