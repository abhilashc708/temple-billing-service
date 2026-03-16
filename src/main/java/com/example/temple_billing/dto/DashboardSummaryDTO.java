package com.example.temple_billing.dto;

import lombok.Data;

@Data
public class DashboardSummaryDTO {
    private Double bookingCash;
    private Double bookingUpi;
    private Double otherIncome;

    private Double bookingTotal;

    private Double donationTotal;

    private Double expenseTotal;

    private Long cashCount;

    private Long upiCount;

    private Long totalCount;

    public DashboardSummaryDTO(Double bookingCash,
                               Double bookingUpi,
                               Double otherIncome,
                               Double bookingTotal,
                               Double donationTotal,
                               Double expenseTotal,
                               Long cashCount,
                               Long upiCount,
                               Long totalCount) {

        this.bookingCash = bookingCash;
        this.bookingUpi = bookingUpi;
        this.otherIncome = otherIncome;
        this.bookingTotal = bookingTotal;
        this.donationTotal = donationTotal;
        this.expenseTotal = expenseTotal;
        this.cashCount = cashCount;
        this.upiCount = upiCount;
        this.totalCount = totalCount;
    }
}
