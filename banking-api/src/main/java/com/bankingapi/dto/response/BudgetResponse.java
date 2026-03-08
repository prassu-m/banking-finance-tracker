package com.bankingapi.dto.response;

import com.bankingapi.enums.BudgetPeriod;
import com.bankingapi.enums.TransactionCategory;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BudgetResponse {
    private UUID id;
    private String name;
    private TransactionCategory category;
    private BigDecimal limitAmount;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private double utilizationPercent;
    private BudgetPeriod period;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private boolean alertEnabled;
    private int alertThresholdPercent;
    private boolean alertTriggered;
    private String description;
    private LocalDateTime createdAt;
}
