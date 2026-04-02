package com.example.temple_billing.repository;

import com.example.temple_billing.entity.Receipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long>,
        JpaSpecificationExecutor<Receipt> {

    Optional<Receipt> findTopByOrderByIdDesc();

    @EntityGraph(attributePaths = {"bookings"})
    Page<Receipt> findByUser_Id(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"bookings"})
    Page<Receipt> findByCreatedDateBetween(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable);

    @EntityGraph(attributePaths = {"bookings"})
    Page<Receipt> findByUser_IdAndCreatedDateBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable);

    Page<Receipt> findAll(Specification<Receipt> spec, Pageable pageable);


    @Query("""
SELECT 
COALESCE(SUM(CASE WHEN r.paymentType = 'CASH' THEN r.totalAmount ELSE 0 END),0),
COALESCE(SUM(CASE WHEN r.paymentType = 'UPI' THEN r.totalAmount ELSE 0 END),0),
SUM(CASE WHEN r.paymentType = 'CASH' THEN 1 ELSE 0 END),
SUM(CASE WHEN r.paymentType = 'UPI' THEN 1 ELSE 0 END)
FROM Receipt r
WHERE r.createdDate >= CURRENT_DATE AND r.paymentStatus = 'SUCCESS'
""")
    Object[] getTodayBookingPaymentSummary();


    Optional<Receipt> findTopByCreatedDateBetweenOrderByIdDesc(
            LocalDateTime start,
            LocalDateTime end
    );
    // 🔥 DAILY CASH + UPI TOTAL
    @Query("""
        SELECT r.paymentType, SUM(r.totalAmount)
        FROM Receipt r
        WHERE DATE(r.createdDate) = :date
        AND r.paymentStatus = com.example.temple_billing.entity.PaymentStatus.SUCCESS
        GROUP BY r.paymentType
    """)
    List<Object[]> getDailyCollection(@Param("date") LocalDate date);
}