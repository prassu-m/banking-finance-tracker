package com.bankingapi.service;

import com.bankingapi.dto.request.TransactionRequest;
import com.bankingapi.dto.response.TransactionResponse;
import com.bankingapi.entity.Account;
import com.bankingapi.entity.Transaction;
import com.bankingapi.entity.User;
import com.bankingapi.enums.AccountType;
import com.bankingapi.enums.TransactionCategory;
import com.bankingapi.enums.TransactionType;
import com.bankingapi.exception.InsufficientFundsException;
import com.bankingapi.repository.AccountRepository;
import com.bankingapi.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    private Account sourceAccount;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        User user = User.builder().email("test@example.com").build();

        sourceAccount = Account.builder()
                .accountNumber("ACC1234567890")
                .name("Checking")
                .type(AccountType.CHECKING)
                .balance(new BigDecimal("1000.00"))
                .currency("USD")
                .user(user)
                .build();
    }

    @Test
    void createDeposit_IncreasesBalance() {
        TransactionRequest request = new TransactionRequest();
        request.setType(TransactionType.DEPOSIT);
        request.setCategory(TransactionCategory.INCOME);
        request.setAmount(new BigDecimal("500.00"));
        request.setSourceAccountId(UUID.randomUUID());

        Transaction savedTxn = Transaction.builder()
                .referenceNumber(UUID.randomUUID().toString())
                .type(TransactionType.DEPOSIT)
                .category(TransactionCategory.INCOME)
                .amount(new BigDecimal("500.00"))
                .currency("USD")
                .sourceAccount(sourceAccount)
                .build();

        when(accountService.getAccountEntityById(any(), any())).thenReturn(sourceAccount);
        when(transactionRepository.save(any())).thenReturn(savedTxn);

        transactionService.createTransaction(userId, request);

        assertThat(sourceAccount.getBalance()).isEqualByComparingTo(new BigDecimal("1500.00"));
    }

    @Test
    void createWithdrawal_WithInsufficientFunds_ThrowsException() {
        TransactionRequest request = new TransactionRequest();
        request.setType(TransactionType.WITHDRAWAL);
        request.setCategory(TransactionCategory.FOOD_AND_DINING);
        request.setAmount(new BigDecimal("2000.00")); // More than balance
        request.setSourceAccountId(UUID.randomUUID());

        when(accountService.getAccountEntityById(any(), any())).thenReturn(sourceAccount);

        assertThatThrownBy(() -> transactionService.createTransaction(userId, request))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("Insufficient funds");
    }

    @Test
    void createTransfer_MovesMoneyBetweenAccounts() {
        Account destination = Account.builder()
                .accountNumber("ACC9876543210")
                .balance(new BigDecimal("200.00"))
                .currency("USD")
                .build();

        UUID destId = UUID.randomUUID();
        TransactionRequest request = new TransactionRequest();
        request.setType(TransactionType.TRANSFER);
        request.setCategory(TransactionCategory.TRANSFER);
        request.setAmount(new BigDecimal("300.00"));
        request.setSourceAccountId(UUID.randomUUID());
        request.setDestinationAccountId(destId);

        Transaction savedTxn = Transaction.builder()
                .referenceNumber(UUID.randomUUID().toString())
                .type(TransactionType.TRANSFER)
                .category(TransactionCategory.TRANSFER)
                .amount(new BigDecimal("300.00"))
                .currency("USD")
                .sourceAccount(sourceAccount)
                .destinationAccount(destination)
                .build();

        when(accountService.getAccountEntityById(any(), any())).thenReturn(sourceAccount);
        when(accountRepository.findById(destId)).thenReturn(java.util.Optional.of(destination));
        when(transactionRepository.save(any())).thenReturn(savedTxn);

        transactionService.createTransaction(userId, request);

        assertThat(sourceAccount.getBalance()).isEqualByComparingTo(new BigDecimal("700.00"));
        assertThat(destination.getBalance()).isEqualByComparingTo(new BigDecimal("500.00"));
    }
}
