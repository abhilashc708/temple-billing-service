package com.example.temple_billing.repository;


import com.example.temple_billing.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>,
        JpaSpecificationExecutor<Expense> {

    Page<Expense> findByCreatedById(Long userId, Pageable pageable);

    long countByReceiptDate(LocalDate receiptDate);

    @Query("""
            SELECT COALESCE(SUM(e.amount),0)
            FROM Expense e
            WHERE DATE(e.createdDate)=CURRENT_DATE
            """)
    Double getTodayExpenseTotal();
}
