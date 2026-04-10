package com.example.temple_billing.controller;

import com.example.temple_billing.dto.DashboardSummaryDTO;
import com.example.temple_billing.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/today")
    public DashboardSummaryDTO getDashboardSummary() {
        logger.debug("Fetching today's dashboard summary");
        DashboardSummaryDTO summary = dashboardService.getTodayDashboard();
        logger.info("Dashboard summary retrieved - Total Income: Rs.{}", summary.getBookingTotal());
        return summary;
    }
}
