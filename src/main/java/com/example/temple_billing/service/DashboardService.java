package com.example.temple_billing.service;

import com.example.temple_billing.dto.DashboardSummaryDTO;
import com.example.temple_billing.repository.DonationRepository;
import com.example.temple_billing.repository.ExpenseRepository;
import com.example.temple_billing.repository.IncomeRepository;
import com.example.temple_billing.repository.ReceiptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

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

        logger.info("Generating today's dashboard summary");

        try {
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

            logger.debug("Today's booking payments - Cash: Rs.{} (Count: {}), UPI: Rs.{} (Count: {})", cash, cashCount, upi, upiCount);

            Object[] donationResult = donationRepository.getTodayDonationSummary();

            double donationCash = 0;
            double donationUpi = 0;
            long donationCashCount = 0;
            long donationUpiCount = 0;

            if (donationResult != null && donationResult.length > 0) {

                Object[] row = (Object[]) donationResult[0];

                if (row.length >= 2) {
                    donationCash = row[0] != null ? ((Number) row[0]).doubleValue() : 0;
                    donationUpi = row[1] != null ? ((Number) row[1]).doubleValue() : 0;
                    donationCashCount = row[2] != null ? ((Number) row[2]).longValue() : 0;
                    donationUpiCount = row[3] != null ? ((Number) row[3]).longValue() : 0;
                }
            }

            logger.debug("Today's donations - Cash: Rs.{} (Count: {}), UPI: Rs.{} (Count: {})", donationCash, donationCashCount, donationUpi, donationUpiCount);

            LocalDate today = LocalDate.now();

            LocalDateTime start = today.atStartOfDay();
            LocalDateTime end = today.plusDays(1).atStartOfDay();


            Double otherIncome = incomeRepository.getTodayIncomeTotal(start, end);
            Double donationTotal = donationRepository.getTodayDonationTotal(start, end);
            Double expenseTotal = expenseRepository.getTodayExpenseTotal(start, end);

            if (otherIncome == null) otherIncome = 0.0;
            if (donationTotal == null) donationTotal = 0.0;
            if (expenseTotal == null) expenseTotal = 0.0;

            logger.debug("Today's summary - Other Income: Rs.{}, Donation Total: Rs.{}, Expense Total: Rs.{}", 
                    otherIncome, donationTotal, expenseTotal);

            double bookingTotal = cash + upi + otherIncome;
            long totalDonationCount = donationCashCount + donationUpiCount;
            long totalCount = cashCount + upiCount;

            logger.info("Dashboard summary generated - Total Income: Rs.{}, Total Expenses: Rs.{}, Total Donations: Rs.{}", 
                    bookingTotal, expenseTotal, donationTotal);

            return new DashboardSummaryDTO(
                    cash,
                    upi,
                    otherIncome,
                    bookingTotal,
                    donationTotal,
                    expenseTotal,
                    cashCount,
                    upiCount,
                    totalCount,
                    donationCash,
                    donationUpi,
                    donationCashCount,
                    donationUpiCount,
                    totalDonationCount
            );
        } catch (Exception e) {
            logger.error("Error generating dashboard summary: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating dashboard summary", e);
        }
    }
}
