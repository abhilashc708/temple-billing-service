package com.example.temple_billing.utility;

import com.example.temple_billing.dto.BookingSearchRequest;
import com.example.temple_billing.entity.Booking;
import com.example.temple_billing.entity.Receipt;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingSpecification {

    public static Specification<Receipt> search(
            String receiptNumber,
            LocalDateTime createdFrom,
            LocalDateTime createdTo,
            String paymentStatus,
            String paymentType
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (receiptNumber != null && !receiptNumber.isEmpty()) {
                predicates.add(cb.equal(
                        root.get("receiptNumber"),
                        receiptNumber));
            }

            // Created Date filter
            if (createdFrom != null && createdTo != null) {
                predicates.add(cb.between(
                        root.get("createdDate"),
                        createdFrom,
                        createdTo));
            } else if (createdFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdDate"),
                        createdFrom));
            } else if (createdTo != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("createdDate"),
                        createdTo));
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

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Receipt> filterBookings(BookingSearchRequest request) {

        return (root, query, cb) -> {

            Join<Receipt, Booking> bookingJoin = root.join("bookings");

            List<Predicate> predicates = new ArrayList<>();

            if (request.getVazhipadu() != null && !request.getVazhipadu().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(bookingJoin.get("vazhipadu")),
                        "%" + request.getVazhipadu().toLowerCase() + "%"
                ));
            }

            if (request.getBirthStar() != null && !request.getBirthStar().isEmpty()) {
                predicates.add(cb.equal(
                        bookingJoin.get("birthStar"),
                        request.getBirthStar()
                ));
            }

            if (request.getVazhipaduFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        bookingJoin.get("bookingDate"),
                        request.getVazhipaduFromDate()
                ));
            }

            if (request.getVazhipaduToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        bookingJoin.get("bookingDate"),
                        request.getVazhipaduToDate()
                ));
            }

            if (request.getBookingFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdDate"),
                        request.getBookingFromDate().atStartOfDay()
                ));
            }

            if (request.getBookingToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("createdDate"),
                        request.getBookingToDate().atTime(23, 59, 59)
                ));
            }

            if (request.getPaymentStatus() != null && !request.getPaymentStatus().toString().isEmpty()) {
                predicates.add(cb.equal(
                        root.get("paymentStatus"),
                        request.getPaymentStatus()
                ));
            }

            if (request.getPaymentType() != null) {
                predicates.add(cb.equal(
                        root.get("paymentType"),
                        request.getPaymentType()
                ));
            }

            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Receipt> search(BookingSearchRequest request) {

        return (root, query, cb) -> {

            Join<Receipt, Booking> booking = root.join("bookings");

            List<Predicate> predicates = new ArrayList<>();

            if (request.getBirthStar() != null) {
                predicates.add(cb.equal(
                        cb.lower(booking.get("birthStar")),
                        request.getBirthStar().toLowerCase()
                ));
            }

            if (request.getVazhipadu() != null) {
                predicates.add(cb.like(
                        cb.lower(booking.get("vazhipadu")),
                        "%" + request.getVazhipadu().toLowerCase() + "%"
                ));
            }

            if (request.getPaymentStatus() != null) {
                predicates.add(cb.equal(
                        root.get("paymentStatus"),
                        request.getPaymentStatus()
                ));
            }

            if (request.getPaymentType() != null) {
                predicates.add(cb.equal(
                        root.get("paymentType"),
                        request.getPaymentType()
                ));
            }

            if (request.getVazhipaduFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        booking.get("bookingDate"),
                        request.getVazhipaduFromDate()
                ));
            }

            if (request.getVazhipaduToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        booking.get("bookingDate"),
                        request.getVazhipaduToDate()
                ));
            }

            if (request.getBookingFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdDate"),
                        request.getBookingFromDate().atStartOfDay()
                ));
            }

            if (request.getBookingToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("createdDate"),
                        request.getBookingToDate().atTime(23, 59, 59)
                ));
            }

            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
