package com.bankingapi.dto.request;

import com.bankingapi.enums.TransactionCategory;
import com.bankingapi.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionRequest {

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @NotNull(message = "Category is required")
    private TransactionCategory category;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 15, fraction = 4)
    private BigDecimal amount;

    @NotNull(message = "Source account is required")
    private UUID sourceAccountId;

    private UUID destinationAccountId;

    @Size(max = 500)
    private String description;

    @Size(max = 100)
    private String merchant;

    private LocalDateTime transactionDate;

    @Size(max = 500)
    private String notes;
}
