package com.bankingapi.service;

import com.bankingapi.dto.request.TransactionRequest;
import com.bankingapi.dto.response.TransactionResponse;
import com.bankingapi.entity.Account;
import com.bankingapi.entity.Transaction;
import com.bankingapi.enums.TransactionType;
import com.bankingapi.exception.InsufficientFundsException;
import com.bankingapi.exception.ResourceNotFoundException;
import com.bankingapi.repository.AccountRepository;
import com.bankingapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Transactional
    public TransactionResponse createTransaction(UUID userId, TransactionRequest request) {
        Account sourceAccount = accountService.getAccountEntityById(request.getSourceAccountId(), userId);

        Account destinationAccount = null;
        if (request.getDestinationAccountId() != null) {
            destinationAccount = accountRepository.findById(request.getDestinationAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getDestinationAccountId()));
        }

        processBalanceChanges(request, sourceAccount, destinationAccount);

        Transaction transaction = Transaction.builder()
                .referenceNumber(UUID.randomUUID().toString())
                .type(request.getType())
                .category(request.getCategory())
                .amount(request.getAmount())
                .currency(sourceAccount.getCurrency())
                .description(request.getDescription())
                .merchant(request.getMerchant())
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .transactionDate(request.getTransactionDate() != null
                        ? request.getTransactionDate()
                        : LocalDateTime.now())
                .notes(request.getNotes())
                .build();

        accountRepository.save(sourceAccount);
        if (destinationAccount != null) {
            accountRepository.save(destinationAccount);
        }

        return toResponse(transactionRepository.save(transaction));
    }

    private void processBalanceChanges(TransactionRequest request, Account source, Account destination) {
        switch (request.getType()) {
            case DEPOSIT -> source.setBalance(source.getBalance().add(request.getAmount()));

            case WITHDRAWAL, PAYMENT, FEE -> {
                if (source.getBalance().compareTo(request.getAmount()) < 0) {
                    throw new InsufficientFundsException(
                            String.format("Insufficient funds. Available: %s %s, Required: %s",
                                    source.getBalance(), source.getCurrency(), request.getAmount()));
                }
                source.setBalance(source.getBalance().subtract(request.getAmount()));
            }

            case TRANSFER -> {
                if (destination == null) {
                    throw new IllegalArgumentException("Destination account is required for transfers");
                }
                if (source.getBalance().compareTo(request.getAmount()) < 0) {
                    throw new InsufficientFundsException(
                            String.format("Insufficient funds. Available: %s %s, Required: %s",
                                    source.getBalance(), source.getCurrency(), request.getAmount()));
                }
                source.setBalance(source.getBalance().subtract(request.getAmount()));
                destination.setBalance(destination.getBalance().add(request.getAmount()));
            }

            case REFUND -> {
                source.setBalance(source.getBalance().add(request.getAmount()));
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getUserTransactions(UUID userId, LocalDateTime startDate,
                                                          LocalDateTime endDate, Pageable pageable) {
        if (startDate != null && endDate != null) {
            return transactionRepository.findByUserIdAndDateRange(userId, startDate, endDate, pageable)
                    .map(this::toResponse);
        }
        return transactionRepository.findBySourceAccountIdOrDestinationAccountId(userId, userId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(UUID transactionId, UUID userId) {
        Transaction txn = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));

        boolean isOwner = (txn.getSourceAccount() != null &&
                txn.getSourceAccount().getUser().getId().equals(userId)) ||
                (txn.getDestinationAccount() != null &&
                        txn.getDestinationAccount().getUser().getId().equals(userId));

        if (!isOwner) {
            throw new ResourceNotFoundException("Transaction", "id", transactionId);
        }

        return toResponse(txn);
    }

    @Transactional
    public TransactionResponse reconcileTransaction(UUID transactionId, UUID userId) {
        Transaction txn = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));
        txn.setReconciled(true);
        return toResponse(transactionRepository.save(txn));
    }

    private TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .referenceNumber(t.getReferenceNumber())
                .type(t.getType())
                .category(t.getCategory())
                .amount(t.getAmount())
                .currency(t.getCurrency())
                .description(t.getDescription())
                .merchant(t.getMerchant())
                .sourceAccountId(t.getSourceAccount() != null ? t.getSourceAccount().getId() : null)
                .sourceAccountNumber(t.getSourceAccount() != null ? t.getSourceAccount().getAccountNumber() : null)
                .destinationAccountId(t.getDestinationAccount() != null ? t.getDestinationAccount().getId() : null)
                .destinationAccountNumber(t.getDestinationAccount() != null ? t.getDestinationAccount().getAccountNumber() : null)
                .transactionDate(t.getTransactionDate())
                .reconciled(t.isReconciled())
                .notes(t.getNotes())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
