package com.example.temple_billing.repository;

import com.example.temple_billing.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>,
        JpaSpecificationExecutor<Booking> {

    Page<Booking> findByReceipt_User_Id(Long userId, Pageable pageable);

    Page<Booking> findByReceipt_User_IdAndBookingDateBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable);

    Page<Booking> findByBookingDateBetween(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable);
}