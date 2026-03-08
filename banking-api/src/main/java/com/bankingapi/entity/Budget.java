package com.bankingapi.entity;

import com.bankingapi.enums.BudgetPeriod;
import com.bankingapi.enums.TransactionCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "budgets", indexes = {
        @Index(name = "idx_budget_user", columnList = "user_id"),
        @Index(name = "idx_budget_category", columnList = "category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionCategory category;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal limitAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetPeriod period;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean alertEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private int alertThresholdPercent = 80;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
