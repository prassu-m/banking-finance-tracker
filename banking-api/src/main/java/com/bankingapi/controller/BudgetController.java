package com.bankingapi.controller;

import com.bankingapi.dto.request.BudgetRequest;
import com.bankingapi.dto.response.BudgetResponse;
import com.bankingapi.service.BudgetService;
import com.bankingapi.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
@Tag(name = "Budgets", description = "Manage spending budgets")
@SecurityRequirement(name = "bearerAuth")
public class BudgetController {

    private final BudgetService budgetService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @Operation(summary = "Create a new budget")
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody BudgetRequest request) {
        UUID userId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.createBudget(userId, request));
    }

    @GetMapping
    @Operation(summary = "Get all active budgets")
    public ResponseEntity<List<BudgetResponse>> getBudgets() {
        UUID userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(budgetService.getUserBudgets(userId));
    }

    @GetMapping("/{budgetId}")
    @Operation(summary = "Get budget by ID with real-time spending data")
    public ResponseEntity<BudgetResponse> getBudget(@PathVariable UUID budgetId) {
        UUID userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(budgetService.getBudgetById(budgetId, userId));
    }

    @PutMapping("/{budgetId}")
    @Operation(summary = "Update a budget")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable UUID budgetId,
            @Valid @RequestBody BudgetRequest request) {
        UUID userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(budgetService.updateBudget(budgetId, userId, request));
    }

    @DeleteMapping("/{budgetId}")
    @Operation(summary = "Delete (deactivate) a budget")
    public ResponseEntity<Void> deleteBudget(@PathVariable UUID budgetId) {
        UUID userId = securityUtils.getCurrentUserId();
        budgetService.deleteBudget(budgetId, userId);
        return ResponseEntity.noContent().build();
    }
}
