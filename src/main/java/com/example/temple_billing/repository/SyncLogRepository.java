package com.example.temple_billing.repository;

import com.example.temple_billing.entity.SyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface SyncLogRepository extends JpaRepository<SyncLog, Long> {

    // 🔥 Get latest sync record
    Optional<SyncLog> findTopByOrderByIdDesc();

}