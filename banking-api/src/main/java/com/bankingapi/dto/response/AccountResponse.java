package com.bankingapi.dto.response;

import com.bankingapi.enums.AccountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AccountResponse {
    private UUID id;
    private String accountNumber;
    private String name;
    private AccountType type;
    private BigDecimal balance;
    private String currency;
    private boolean active;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
