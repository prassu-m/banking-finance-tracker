package com.bankingapi.dto.response;

import com.bankingapi.enums.TransactionCategory;
import com.bankingapi.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransactionResponse {
    private UUID id;
    private String referenceNumber;
    private TransactionType type;
    private TransactionCategory category;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String merchant;
    private UUID sourceAccountId;
    private String sourceAccountNumber;
    private UUID destinationAccountId;
    private String destinationAccountNumber;
    private LocalDateTime transactionDate;
    private boolean reconciled;
    private String notes;
    private LocalDateTime createdAt;
}
