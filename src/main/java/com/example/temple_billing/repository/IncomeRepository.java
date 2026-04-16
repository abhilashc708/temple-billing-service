package com.example.temple_billing.repository;

import com.example.temple_billing.entity.Income;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long>,
        JpaSpecificationExecutor<Income> {

    Page<Income> findByCreatedById(Long userId, Pageable pageable);

    long countByReceiptDate(LocalDate receiptDate);

@Query("""
SELECT 
    COALESCE(SUM(CASE WHEN i.modeOfIncome = 'CASH' THEN i.amount END),0),
    COALESCE(SUM(CASE WHEN i.modeOfIncome = 'UPI' THEN i.amount END),0),
    COUNT(CASE WHEN i.modeOfIncome = 'CASH' THEN 1 END),
    COUNT(CASE WHEN i.modeOfIncome = 'UPI' THEN 1 END)
FROM Income i
WHERE i.createdDate BETWEEN :start AND :end
AND i.incomeType NOT IN ('Vazhipadu', 'Donation')
""")
Object[] getTodayOtherIncomeSummary(LocalDateTime start, LocalDateTime end);

Optional<Income> findByReceiptDateAndModeOfIncomeAndIncomeType(
        LocalDate date,
        String mode,
        String incomeType
);
}
