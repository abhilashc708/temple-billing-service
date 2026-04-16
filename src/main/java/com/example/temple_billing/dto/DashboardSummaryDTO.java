package com.example.temple_billing.dto;

import lombok.Data;

@Data
public class DashboardSummaryDTO {
    private Double bookingCash;
    private Double bookingUpi;
    private Double donationCash;
    private Double donationUpi;

    private Double bookingTotal;

    private Double donationTotal;

    private Double expenseTotal;

    private Long cashCount;

    private Long upiCount;
    private Long donationCashCount;

    private Long donationUpiCount;

    private Long totalCount;

    private Long totalDonationCount;

    private double otherCash;
    private double otherUpi;
    private long otherCashCount;
    private long otherUpiCount;

    public DashboardSummaryDTO(Double bookingCash,
                               Double bookingUpi,
                               Double bookingTotal,
                               Double donationTotal,
                               Double expenseTotal,
                               Long cashCount,
                               Long upiCount,
                               Long totalCount,
                               Double donationCash,
                               Double donationUpi,
                               Long donationCashCount,
                               Long donationUpiCount,
                               Long totalDonationCount,
                               Double otherCash,
                               Double otherUpi,
                               Long otherCashCount,
                               Long otherUpiCount) {

        this.bookingCash = bookingCash;
        this.bookingUpi = bookingUpi;
        this.bookingTotal = bookingTotal;
        this.donationTotal = donationTotal;
        this.expenseTotal = expenseTotal;
        this.cashCount = cashCount;
        this.upiCount = upiCount;
        this.totalCount = totalCount;
        this.donationCash = donationCash;
        this.donationUpi = donationUpi;
        this.donationCashCount = donationCashCount;
        this.donationUpiCount = donationUpiCount;
        this.totalDonationCount = totalDonationCount;
        this.otherCash = otherCash;
        this.otherUpi = otherUpi;
        this.otherCashCount = otherCashCount;
        this.otherUpiCount = otherUpiCount;
    }
}
