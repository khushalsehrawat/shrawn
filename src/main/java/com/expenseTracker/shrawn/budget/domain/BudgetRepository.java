package com.expenseTracker.shrawn.budget.domain;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository {

    Budget create(Budget budget);

    Optional<Budget> findByIdAndUserId(
            UUID budgetId,
            UUID userId
    );

    boolean existsOverlappingBudget(
            UUID userId,
            UUID categoryId,
            LocalDate startDate,
            LocalDate endDate
    );

    boolean existsOverlappingBudgetExcludingId(
            UUID userId,
            UUID categoryId,
            LocalDate startDate,
            LocalDate endDate,
            UUID excludedBudgetId
    );

    List<Budget> findAllByUserId(UUID userId);

    List<Budget> findAllActiveByUserId(UUID userId);

    List<Budget> findActiveBudgetsForDate(
            UUID userId,
            LocalDate date
    );

    Budget save(Budget budget);
}