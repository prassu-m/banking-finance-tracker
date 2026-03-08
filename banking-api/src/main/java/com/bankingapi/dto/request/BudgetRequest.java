package com.bankingapi.dto.request;

import com.bankingapi.enums.BudgetPeriod;
import com.bankingapi.enums.TransactionCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BudgetRequest {

    @NotBlank(message = "Budget name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotNull(message = "Category is required")
    private TransactionCategory category;

    @NotNull(message = "Limit amount is required")
    @DecimalMin(value = "0.01", message = "Limit must be greater than 0")
    @Digits(integer = 15, fraction = 4)
    private BigDecimal limitAmount;

    @NotNull(message = "Period is required")
    private BudgetPeriod period;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private boolean alertEnabled = true;

    @Min(1) @Max(100)
    private int alertThresholdPercent = 80;

    @Size(max = 500)
    private String description;
}
