package com.bankingapi.controller;

import com.bankingapi.dto.response.FinancialSummaryResponse;
import com.bankingapi.service.AnalyticsService;
import com.bankingapi.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Financial reports and analytics")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final SecurityUtils securityUtils;

    @GetMapping("/summary")
    @Operation(summary = "Get financial summary with income, expenses, and monthly trends")
    public ResponseEntity<FinancialSummaryResponse> getFinancialSummary(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().minusMonths(6)}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        UUID userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(analyticsService.getFinancialSummary(userId, startDate, endDate));
    }
}
