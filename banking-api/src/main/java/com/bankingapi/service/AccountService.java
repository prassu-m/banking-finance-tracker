package com.bankingapi.service;

import com.bankingapi.dto.request.AccountRequest;
import com.bankingapi.dto.response.AccountResponse;
import com.bankingapi.entity.Account;
import com.bankingapi.entity.User;
import com.bankingapi.exception.ResourceNotFoundException;
import com.bankingapi.exception.UnauthorizedAccessException;
import com.bankingapi.repository.AccountRepository;
import com.bankingapi.repository.UserRepository;
import com.bankingapi.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    @Transactional
    public AccountResponse createAccount(UUID userId, AccountRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String accountNumber;
        do {
            accountNumber = accountNumberGenerator.generate();
        } while (accountRepository.existsByAccountNumber(accountNumber));

        Account account = Account.builder()
                .name(request.getName())
                .type(request.getType())
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .description(request.getDescription())
                .accountNumber(accountNumber)
                .user(user)
                .build();

        return toResponse(accountRepository.save(account));
    }

    @Cacheable(value = "accounts", key = "#userId")
    @Transactional(readOnly = true)
    public List<AccountResponse> getUserAccounts(UUID userId) {
        return accountRepository.findByUserIdAndActiveTrue(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(UUID accountId, UUID userId) {
        Account account = accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        return toResponse(account);
    }

    @Transactional
    @CacheEvict(value = "accounts", key = "#userId")
    public AccountResponse updateAccount(UUID accountId, UUID userId, AccountRequest request) {
        Account account = accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));

        account.setName(request.getName());
        account.setDescription(request.getDescription());
        return toResponse(accountRepository.save(account));
    }

    @Transactional
    @CacheEvict(value = "accounts", key = "#userId")
    public void deactivateAccount(UUID accountId, UUID userId) {
        Account account = accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        account.setActive(false);
        accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalBalance(UUID userId) {
        return accountRepository.sumBalanceByUserId(userId).orElse(BigDecimal.ZERO);
    }

    public Account getAccountEntityById(UUID accountId, UUID userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        if (!account.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You do not have access to this account");
        }
        return account;
    }

    private AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .name(account.getName())
                .type(account.getType())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .active(account.isActive())
                .description(account.getDescription())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
