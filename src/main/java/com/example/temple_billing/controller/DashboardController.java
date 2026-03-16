package com.example.temple_billing.controller;

import com.example.temple_billing.dto.DashboardSummaryDTO;
import com.example.temple_billing.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/today")
    public DashboardSummaryDTO getDashboardSummary() {
        return dashboardService.getTodayDashboard();
    }
}
