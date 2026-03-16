package com.example.temple_billing.repository;

import com.example.temple_billing.entity.God;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GodRepository extends JpaRepository<God, Long> {
}