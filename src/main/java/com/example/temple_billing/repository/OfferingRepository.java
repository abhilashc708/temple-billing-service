package com.example.temple_billing.repository;

import com.example.temple_billing.entity.Offering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferingRepository extends JpaRepository<Offering, Long> {
    Page<Offering> findByStatus(String status, Pageable pageable);

    Page<Offering> findAll(Specification<Offering> spec, Pageable pageable);
}