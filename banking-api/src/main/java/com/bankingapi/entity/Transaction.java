package com.bankingapi.entity;

import com.bankingapi.enums.TransactionCategory;
import com.bankingapi.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_txn_source_account", columnList = "source_account_id"),
        @Index(name = "idx_txn_destination_account", columnList = "destination_account_id"),
        @Index(name = "idx_txn_created_at", columnList = "createdAt"),
        @Index(name = "idx_txn_reference", columnList = "referenceNumber", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity {

    @Column(nullable = false, unique = true, length = 36)
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionCategory category;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Column(length = 500)
    private String description;

    @Column(length = 100)
    private String merchant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean reconciled = false;

    @Column(length = 500)
    private String notes;
}
