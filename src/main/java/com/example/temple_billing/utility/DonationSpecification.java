package com.example.temple_billing.utility;

import com.example.temple_billing.dto.DonationSearchRequest;
import com.example.temple_billing.entity.Donation;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DonationSpecification {

    public static Specification<Donation> search(
            String devoteeName,
            String paymentStatus,
            String paymentType,
            LocalDate donationFrom,
            LocalDate donationTo,
            String donateFor,
            String receiptBookNo
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (devoteeName != null && !devoteeName.isEmpty()) {
                predicates.add(cb.equal(
                        root.get("devoteeName"),
                        devoteeName));
            }

            if (paymentStatus != null && !paymentStatus.isEmpty()) {
                predicates.add(cb.equal(
                        root.get("paymentStatus"),
                        paymentStatus));
            }

            if (paymentType != null && !paymentType.isEmpty()) {
                predicates.add(cb.equal(
                        root.get("paymentType"),
                        paymentType));
            }

            // Donation Date Range
            if (donationFrom != null && donationTo != null) {
                predicates.add(cb.between(
                        root.get("donationDate"),
                        donationFrom,
                        donationTo));
            } else if (donationFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("donationDate"),
                        donationFrom));
            } else if (donationTo != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("donationDate"),
                        donationTo));
            }

            if (donateFor != null && !donateFor.isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("donateFor")),
                        "%" + donateFor.toLowerCase() + "%"));
            }

            if (receiptBookNo != null && !receiptBookNo.isEmpty()) {
                predicates.add(cb.equal(
                        root.get("receiptBookNo"),
                        receiptBookNo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Donation> search(DonationSearchRequest request) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getPaymentStatus() != null && !request.getPaymentStatus().isEmpty()) {
                predicates.add(cb.equal(
                        root.get("paymentStatus"),
                        request.getPaymentStatus()));
            }

            if (request.getPaymentType() != null && !request.getPaymentType().isEmpty()) {
                predicates.add(cb.equal(
                        root.get("paymentType"),
                        request.getPaymentType()));
            }

            // Donation Date Range
            if (request.getDonationFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("donationDate"),
                        request.getDonationFromDate()
                ));
            }

            if (request.getDonationToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("donationDate"),
                        request.getDonationToDate()
                ));
            }

            if (request.getDonateFor() != null && !request.getDonateFor().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("donateFor")),
                        "%" + request.getDonateFor().toLowerCase() + "%"));
            }

            if (request.getReceiptBookNumber() != null && !request.getReceiptBookNumber().isEmpty()) {
                predicates.add(cb.equal(
                        root.get("receiptBookNo"),
                        request.getReceiptBookNumber()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
