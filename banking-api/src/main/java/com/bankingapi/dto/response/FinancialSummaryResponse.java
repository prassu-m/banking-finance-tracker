package com.bankingapi.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class FinancialSummaryResponse {
    private BigDecimal totalBalance;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netCashFlow;
    private int totalAccounts;
    private int totalTransactions;
    private Map<String, BigDecimal> expensesByCategory;
    private List<MonthlySummary> monthlyTrend;

    @Data
    @Builder
    public static class MonthlySummary {
        private int year;
        private int month;
        private String monthName;
        private BigDecimal totalSpending;
    }
}
