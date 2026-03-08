package com.bankingapi.repository;

import com.bankingapi.entity.Transaction;
import com.bankingapi.enums.TransactionCategory;
import com.bankingapi.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findBySourceAccountIdOrDestinationAccountId(
            UUID sourceId, UUID destId, Pageable pageable);

    @Query("""
            SELECT t FROM Transaction t
            WHERE (t.sourceAccount.user.id = :userId OR t.destinationAccount.user.id = :userId)
            AND t.transactionDate BETWEEN :startDate AND :endDate
            ORDER BY t.transactionDate DESC
            """)
    Page<Transaction> findByUserIdAndDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("""
            SELECT t FROM Transaction t
            WHERE t.sourceAccount.user.id = :userId
            AND t.category = :category
            AND t.transactionDate BETWEEN :startDate AND :endDate
            """)
    List<Transaction> findByUserIdAndCategoryAndDateRange(
            @Param("userId") UUID userId,
            @Param("category") TransactionCategory category,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("""
            SELECT t.category, SUM(t.amount)
            FROM Transaction t
            WHERE t.sourceAccount.user.id = :userId
            AND t.type IN (:types)
            AND t.transactionDate BETWEEN :startDate AND :endDate
            GROUP BY t.category
            """)
    List<Object[]> sumAmountByCategoryForUser(
            @Param("userId") UUID userId,
            @Param("types") List<TransactionType> types,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.sourceAccount.user.id = :userId
            AND t.category = :category
            AND t.transactionDate BETWEEN :startDate AND :endDate
            """)
    BigDecimal sumSpendingByCategoryAndDateRange(
            @Param("userId") UUID userId,
            @Param("category") TransactionCategory category,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("""
            SELECT FUNCTION('MONTH', t.transactionDate) as month,
                   FUNCTION('YEAR', t.transactionDate) as year,
                   SUM(t.amount) as total
            FROM Transaction t
            WHERE t.sourceAccount.user.id = :userId
            AND t.type IN ('WITHDRAWAL', 'PAYMENT', 'FEE')
            AND t.transactionDate BETWEEN :startDate AND :endDate
            GROUP BY FUNCTION('YEAR', t.transactionDate), FUNCTION('MONTH', t.transactionDate)
            ORDER BY year, month
            """)
    List<Object[]> getMonthlySpendingSummary(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
