package com.bankingapi.service;

import com.bankingapi.dto.request.BudgetRequest;
import com.bankingapi.dto.response.BudgetResponse;
import com.bankingapi.entity.Budget;
import com.bankingapi.entity.User;
import com.bankingapi.exception.ResourceNotFoundException;
import com.bankingapi.repository.BudgetRepository;
import com.bankingapi.repository.TransactionRepository;
import com.bankingapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public BudgetResponse createBudget(UUID userId, BudgetRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Budget budget = Budget.builder()
                .name(request.getName())
                .category(request.getCategory())
                .limitAmount(request.getLimitAmount())
                .period(request.getPeriod())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .alertEnabled(request.isAlertEnabled())
                .alertThresholdPercent(request.getAlertThresholdPercent())
                .description(request.getDescription())
                .user(user)
                .build();

        return toResponse(budgetRepository.save(budget), userId);
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getUserBudgets(UUID userId) {
        return budgetRepository.findByUserIdAndActiveTrue(userId)
                .stream()
                .map(b -> toResponse(b, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(UUID budgetId, UUID userId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        return toResponse(budget, userId);
    }

    @Transactional
    public BudgetResponse updateBudget(UUID budgetId, UUID userId, BudgetRequest request) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));

        budget.setName(request.getName());
        budget.setLimitAmount(request.getLimitAmount());
        budget.setAlertEnabled(request.isAlertEnabled());
        budget.setAlertThresholdPercent(request.getAlertThresholdPercent());
        budget.setDescription(request.getDescription());

        return toResponse(budgetRepository.save(budget), userId);
    }

    @Transactional
    public void deleteBudget(UUID budgetId, UUID userId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        budget.setActive(false);
        budgetRepository.save(budget);
    }

    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    @Transactional
    public void deactivateExpiredBudgets() {
        List<Budget> expired = budgetRepository.findByActiveTrueAndEndDateBefore(LocalDate.now());
        expired.forEach(b -> b.setActive(false));
        budgetRepository.saveAll(expired);
        log.info("Deactivated {} expired budgets", expired.size());
    }

    private BigDecimal calculateSpending(Budget budget, UUID userId) {
        return transactionRepository.sumSpendingByCategoryAndDateRange(
                userId,
                budget.getCategory(),
                budget.getStartDate().atStartOfDay(),
                budget.getEndDate().atTime(23, 59, 59));
    }

    private BudgetResponse toResponse(Budget budget, UUID userId) {
        BigDecimal spent = calculateSpending(budget, userId);
        BigDecimal remaining = budget.getLimitAmount().subtract(spent);
        double utilization = budget.getLimitAmount().compareTo(BigDecimal.ZERO) > 0
                ? spent.divide(budget.getLimitAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0;

        return BudgetResponse.builder()
                .id(budget.getId())
                .name(budget.getName())
                .category(budget.getCategory())
                .limitAmount(budget.getLimitAmount())
                .spentAmount(spent)
                .remainingAmount(remaining)
                .utilizationPercent(utilization)
                .period(budget.getPeriod())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .active(budget.isActive())
                .alertEnabled(budget.isAlertEnabled())
                .alertThresholdPercent(budget.getAlertThresholdPercent())
                .alertTriggered(budget.isAlertEnabled() && utilization >= budget.getAlertThresholdPercent())
                .description(budget.getDescription())
                .createdAt(budget.getCreatedAt())
                .build();
    }
}
