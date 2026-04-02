package com.example.temple_billing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class SyncLog {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate lastSyncedDate;
}