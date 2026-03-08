package com.bankingapi.repository;

import com.bankingapi.entity.Budget;
import com.bankingapi.enums.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID>, JpaSpecificationExecutor<Budget> {

    List<Budget> findByUserIdAndActiveTrue(UUID userId);

    Optional<Budget> findByIdAndUserId(UUID id, UUID userId);

    List<Budget> findByUserIdAndCategoryAndActiveTrue(UUID userId, TransactionCategory category);

    List<Budget> findByActiveTrueAndEndDateBefore(LocalDate date);

    List<Budget> findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            UUID userId, LocalDate currentDate1, LocalDate currentDate2);
}
