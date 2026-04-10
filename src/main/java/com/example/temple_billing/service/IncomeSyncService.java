package com.example.temple_billing.service;

import com.example.temple_billing.entity.Income;
import com.example.temple_billing.entity.SyncLog;
import com.example.temple_billing.repository.DonationRepository;
import com.example.temple_billing.repository.IncomeRepository;
import com.example.temple_billing.repository.ReceiptRepository;
import com.example.temple_billing.repository.SyncLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeSyncService {
    private final ReceiptRepository receiptRepository;
    private final IncomeRepository incomeRepository;
    private final SyncLogRepository syncLogRepository;
    private final DonationRepository donationRepository;

    @Transactional
    public String syncIncome() {

        LocalDate today = LocalDate.now();
        int totalUpdated = 0;

        try {

            // Sync today
            totalUpdated += syncForDate(today);

            // Sync missed days
            LocalDate lastSyncDate = syncLogRepository.findTopByOrderByIdDesc()
                    .map(SyncLog::getLastSyncedDate)
                    .orElse(today.minusDays(7));

            for (LocalDate date = lastSyncDate.plusDays(1);
                 date.isBefore(today);
                 date = date.plusDays(1)) {

                try {
                    totalUpdated += syncForDate(date);
                } catch (Exception e) {
                    System.err.println("Error syncing date: " + date + " → " + e.getMessage());
                }
            }

            // Update sync log
            if (!today.equals(lastSyncDate)) {
                SyncLog log = syncLogRepository.findTopByOrderByIdDesc()
                        .orElse(new SyncLog());

                log.setLastSyncedDate(today);
                syncLogRepository.save(log);
            }

            // Return message
            if (totalUpdated == 0) {
                return "Already synced. No changes found";
            } else {
                return "Income synced successfully (" + totalUpdated + " records updated)";
            }

        } catch (Exception e) {
            throw new RuntimeException("Income sync failed: " + e.getMessage());
        }
    }

    private int syncForDate(LocalDate date) {

        int updatedCount = 0;

        // Always sync today
        List<Object[]> bookingResults =
                receiptRepository.getDailyCollection(date);

        updatedCount += processIncome(bookingResults, date,
                "Vazhipadu",
                "Being Vazhipadu Amount Received from Devotees via Counter");

        // Sync donations
        List<Object[]> donationResults =
                donationRepository.getDailyDonation(date);

        updatedCount += processIncome(donationResults, date,
                "Donation",
                "Being Donation Amount Received from Devotees via Counter");

        return updatedCount;
    }

    private int processIncome(List<Object[]> results,
                              LocalDate date,
                              String incomeType,
                              String remarks) {

        int updatedCount = 0;

        if (results == null || results.isEmpty()) {
            return 0;
        }

        for (Object[] row : results) {

            try {
                String paymentType = (String) row[0];
                Double total = (Double) row[1];

                Income existing = incomeRepository
                        .findByReceiptDateAndModeOfIncomeAndIncomeType(
                                date, paymentType, incomeType)
                        .orElse(null);

                if (existing != null) {

                    if (!existing.getAmount().equals(total)) {
                        existing.setAmount(total);
                        incomeRepository.save(existing);
                        updatedCount++;
                    }

                } else {

                    Income income = Income.builder()
                            .receiptNo(generateReceiptNo(date))
                            .receiptDate(date)
                            .incomeType(incomeType)
                            .modeOfIncome(paymentType)
                            .amount(total)
                            .remarks(remarks)
                            .createdDate(date.atTime(20, 0))
                            .build();

                    incomeRepository.save(income);
                    updatedCount++;
                }

            } catch (Exception e) {
                System.err.println("Error processing row: " + e.getMessage());
            }
        }

         return updatedCount;
    }


    private String generateReceiptNo(LocalDate date) {
        long count =
                incomeRepository.countByReceiptDate(date);

        long nextNumber = count + 1;

        String formattedNumber =
                String.format("%02d", nextNumber);

        String formattedDate =
                date.format(
                        DateTimeFormatter.ofPattern("ddMMyyyy"));

        return "R-" + formattedNumber + "/" + formattedDate;
    }
}
