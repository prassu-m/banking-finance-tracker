package com.bankingapi.entity;

import com.bankingapi.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts", indexes = {
        @Index(name = "idx_account_number", columnList = "accountNumber", unique = true),
        @Index(name = "idx_account_user", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "sourceAccount", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Transaction> outgoingTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "destinationAccount", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Transaction> incomingTransactions = new ArrayList<>();
}
