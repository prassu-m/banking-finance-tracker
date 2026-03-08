package com.bankingapi.dto.request;

import com.bankingapi.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AccountRequest {

    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotNull(message = "Account type is required")
    private AccountType type;

    @Size(max = 3)
    private String currency = "USD";

    @Size(max = 500)
    private String description;
}
