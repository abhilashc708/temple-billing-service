package com.example.temple_billing.repository;

import com.example.temple_billing.entity.FinanceMaster;
import com.example.temple_billing.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinanceMasterRepository extends JpaRepository<FinanceMaster, Long> {

    Page<FinanceMaster> findByTransactionType(
            TransactionType type,
            Pageable pageable);

    Page<FinanceMaster> findAll(Specification<FinanceMaster> spec, Pageable pageable);
}
