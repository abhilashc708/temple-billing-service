package com.example.temple_billing.service;

import com.example.temple_billing.dto.DashboardSummaryDTO;
import com.example.temple_billing.repository.DonationRepository;
import com.example.temple_billing.repository.ExpenseRepository;
import com.example.temple_billing.repository.IncomeRepository;
import com.example.temple_billing.repository.ReceiptRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DashboardService {

    private final ReceiptRepository receiptRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final DonationRepository donationRepository;


    public DashboardService(ReceiptRepository receiptRepository,
                            IncomeRepository incomeRepository,
                            ExpenseRepository expenseRepository,
                            DonationRepository donationRepository) {
        this.receiptRepository = receiptRepository;
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.donationRepository = donationRepository;
    }

    public DashboardSummaryDTO getTodayDashboard() {

        Object[] result = receiptRepository.getTodayBookingPaymentSummary();

        double cash = 0;
        double upi = 0;
        long cashCount = 0;
        long upiCount = 0;

        if (result != null && result.length > 0) {

            Object[] row = (Object[]) result[0];

            if (row.length >= 2) {
                cash = row[0] != null ? ((Number) row[0]).doubleValue() : 0;
                upi = row[1] != null ? ((Number) row[1]).doubleValue() : 0;
                cashCount = row[2] != null ? ((Number) row[2]).longValue() : 0;
                upiCount = row[3] != null ? ((Number) row[3]).longValue() : 0;
            }
        }

        LocalDate today = LocalDate.now();

        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();


//        Double otherIncome = incomeRepository.getTodayIncomeTotal();
//        Double donationTotal = donationRepository.getTodayDonationTotal();
//        Double expenseTotal = expenseRepository.getTodayExpenseTotal();

        Double otherIncome = incomeRepository.getTodayIncomeTotal(start, end);
        Double donationTotal = donationRepository.getTodayDonationTotal();
        Double expenseTotal = expenseRepository.getTodayExpenseTotal(start, end);

        if (otherIncome == null) otherIncome = 0.0;
        if (donationTotal == null) donationTotal = 0.0;
        if (expenseTotal == null) expenseTotal = 0.0;

        double bookingTotal = cash + upi + otherIncome;
        long totalCount = cashCount + upiCount;

        return new DashboardSummaryDTO(
                cash,
                upi,
                otherIncome,
                bookingTotal,
                donationTotal,
                expenseTotal,
                cashCount,
                upiCount,
                totalCount
        );
    }
}
