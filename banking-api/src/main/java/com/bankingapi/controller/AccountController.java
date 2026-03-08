package com.bankingapi.controller;

import com.bankingapi.dto.request.AccountRequest;
import com.bankingapi.dto.response.AccountResponse;
import com.bankingapi.service.AccountService;
import com.bankingapi.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Manage bank accounts")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AccountService accountService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @Operation(summary = "Create a new account")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {
        UUID userId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(userId, request));
    }

    @GetMapping
    @Operation(summary = "Get all accounts for the current user")
    public ResponseEntity<List<AccountResponse>> getAccounts() {
        UUID userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(accountService.getUserAccounts(userId));
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get account by ID")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable UUID accountId) {
        UUID userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(accountService.getAccountById(accountId, userId));
    }

    @PutMapping("/{accountId}")
    @Operation(summary = "Update account details")
    public ResponseEntity<AccountResponse> updateAccount(
            @PathVariable UUID accountId,
            @Valid @RequestBody AccountRequest request) {
        UUID userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(accountService.updateAccount(accountId, userId, request));
    }

    @DeleteMapping("/{accountId}")
    @Operation(summary = "Deactivate an account")
    public ResponseEntity<Void> deactivateAccount(@PathVariable UUID accountId) {
        UUID userId = securityUtils.getCurrentUserId();
        accountService.deactivateAccount(accountId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total-balance")
    @Operation(summary = "Get total balance across all accounts")
    public ResponseEntity<Map<String, BigDecimal>> getTotalBalance() {
        UUID userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(Map.of("totalBalance", accountService.getTotalBalance(userId)));
    }
}
