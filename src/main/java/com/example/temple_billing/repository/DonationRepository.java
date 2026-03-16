package com.example.temple_billing.repository;

import com.example.temple_billing.entity.Donation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository
        extends JpaRepository<Donation, Long>,
        JpaSpecificationExecutor<Donation> {

    Page<Donation> findByCreatedBy_Id(Long userId, Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(d.amount),0)
            FROM Donation d
            WHERE DATE(d.createdDate)=CURRENT_DATE
            """)
    Double getTodayDonationTotal();
}
