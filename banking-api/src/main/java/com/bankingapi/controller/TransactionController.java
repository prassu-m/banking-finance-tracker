package com.bankingapi.controller;

import com.bankingapi.dto.request.TransactionRequest;
import com.bankingapi.dto.response.TransactionResponse;
import com.bankingapi.service.TransactionService;
import com.bankingapi.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Manage financial transactions")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @Operation(summary = "Create a new transaction (deposit, withdrawal, transfer, etc.)")
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request) {
        UUID userId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.createTransaction(userId, request));
    }

    @GetMapping
    @Operation(summary = "Get paginated transactions with optional date filter")
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "transactionDate") Pageable pageable) {
        UUID userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.getUserTransactions(userId, startDate, endDate, pageable));
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get a transaction by ID")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable UUID transactionId) {
        UUID userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.getTransactionById(transactionId, userId));
    }

    @PatchMapping("/{transactionId}/reconcile")
    @Operation(summary = "Mark a transaction as reconciled")
    public ResponseEntity<TransactionResponse> reconcileTransaction(@PathVariable UUID transactionId) {
        UUID userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.reconcileTransaction(transactionId, userId));
    }
}
