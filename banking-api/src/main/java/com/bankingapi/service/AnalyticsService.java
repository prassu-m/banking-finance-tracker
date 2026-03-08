package com.bankingapi.service;

import com.bankingapi.dto.response.FinancialSummaryResponse;
import com.bankingapi.enums.TransactionType;
import com.bankingapi.repository.AccountRepository;
import com.bankingapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public FinancialSummaryResponse getFinancialSummary(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal totalBalance = accountRepository.sumBalanceByUserId(userId).orElse(BigDecimal.ZERO);

        List<TransactionType> expenseTypes = List.of(
                TransactionType.WITHDRAWAL, TransactionType.PAYMENT, TransactionType.FEE);
        List<TransactionType> incomeTypes = List.of(
                TransactionType.DEPOSIT, TransactionType.REFUND);

        // Expense breakdown by category
        List<Object[]> expensesByCategory = transactionRepository
                .sumAmountByCategoryForUser(userId, expenseTypes, startDate, endDate);

        Map<String, BigDecimal> categoryMap = expensesByCategory.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> (BigDecimal) row[1]));

        BigDecimal totalExpenses = categoryMap.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Income total
        List<Object[]> incomeRows = transactionRepository
                .sumAmountByCategoryForUser(userId, incomeTypes, startDate, endDate);
        BigDecimal totalIncome = incomeRows.stream()
                .map(row -> (BigDecimal) row[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Monthly trend
        List<Object[]> monthlyData = transactionRepository
                .getMonthlySpendingSummary(userId, startDate, endDate);

        List<FinancialSummaryResponse.MonthlySummary> monthlyTrend = monthlyData.stream()
                .map(row -> FinancialSummaryResponse.MonthlySummary.builder()
                        .month(((Number) row[0]).intValue())
                        .year(((Number) row[1]).intValue())
                        .monthName(Month.of(((Number) row[0]).intValue()).name())
                        .totalSpending((BigDecimal) row[2])
                        .build())
                .collect(Collectors.toList());

        int totalAccounts = accountRepository.findByUserIdAndActiveTrue(userId).size();

        return FinancialSummaryResponse.builder()
                .totalBalance(totalBalance)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netCashFlow(totalIncome.subtract(totalExpenses))
                .totalAccounts(totalAccounts)
                .totalTransactions(monthlyData.size())
                .expensesByCategory(categoryMap)
                .monthlyTrend(monthlyTrend)
                .build();
    }
}
