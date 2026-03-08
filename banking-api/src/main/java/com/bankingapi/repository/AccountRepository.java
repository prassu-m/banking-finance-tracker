package com.bankingapi.repository;

import com.bankingapi.entity.Account;
import com.bankingapi.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {

    List<Account> findByUserIdAndActiveTrue(UUID userId);

    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    List<Account> findByUserIdAndType(UUID userId, AccountType type);

    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.user.id = :userId AND a.active = true")
    Optional<BigDecimal> sumBalanceByUserId(@Param("userId") UUID userId);

    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.id = :accountId AND a.active = true")
    Optional<Account> findByIdAndUserId(@Param("accountId") UUID accountId, @Param("userId") UUID userId);
}
