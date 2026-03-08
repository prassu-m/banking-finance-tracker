package com.bankingapi.service;

import com.bankingapi.dto.request.AccountRequest;
import com.bankingapi.dto.response.AccountResponse;
import com.bankingapi.entity.Account;
import com.bankingapi.entity.User;
import com.bankingapi.enums.AccountType;
import com.bankingapi.exception.ResourceNotFoundException;
import com.bankingapi.repository.AccountRepository;
import com.bankingapi.repository.UserRepository;
import com.bankingapi.util.AccountNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @InjectMocks
    private AccountService accountService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("encoded")
                .build();
    }

    @Test
    void createAccount_WithValidData_ReturnsAccountResponse() {
        AccountRequest request = new AccountRequest();
        request.setName("My Savings");
        request.setType(AccountType.SAVINGS);
        request.setCurrency("USD");

        Account savedAccount = Account.builder()
                .accountNumber("ACC1234567890")
                .name("My Savings")
                .type(AccountType.SAVINGS)
                .currency("USD")
                .balance(BigDecimal.ZERO)
                .user(testUser)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(accountNumberGenerator.generate()).thenReturn("ACC1234567890");
        when(accountRepository.existsByAccountNumber(any())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        AccountResponse response = accountService.createAccount(userId, request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("My Savings");
        assertThat(response.getType()).isEqualTo(AccountType.SAVINGS);
        assertThat(response.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_WithInvalidUserId_ThrowsNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        AccountRequest request = new AccountRequest();
        request.setName("Test");
        request.setType(AccountType.CHECKING);

        assertThatThrownBy(() -> accountService.createAccount(userId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getUserAccounts_ReturnsOnlyActiveAccounts() {
        Account active = Account.builder().name("Active").active(true).build();
        when(accountRepository.findByUserIdAndActiveTrue(userId)).thenReturn(List.of(active));

        List<AccountResponse> result = accountService.getUserAccounts(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Active");
    }

    @Test
    void getTotalBalance_ReturnsCorrectSum() {
        when(accountRepository.sumBalanceByUserId(userId))
                .thenReturn(Optional.of(new BigDecimal("5000.00")));

        BigDecimal total = accountService.getTotalBalance(userId);

        assertThat(total).isEqualByComparingTo(new BigDecimal("5000.00"));
    }
}
