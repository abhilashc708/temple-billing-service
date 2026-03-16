package com.example.temple_billing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "receipts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String receiptNumber; // 6 digit

    private LocalDateTime createdDate;

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String paymentType;     // 🔥 MOVED HERE

    private String phoneNumber;     // 🔥 MOVED HERE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User user;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Booking> bookings;
}