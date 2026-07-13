package com.expenseTracker.shrawn.expense.domain;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ExpenseRepository {

    Expense create(
            UUID userId,
            Expense expense
    );

    Optional<Expense> findByIdAndUserId(
            UUID expenseId,
            UUID userId
    );

    Page<Expense> search(
            UUID userId,
            ExpenseSearchCriteria criteria,
            Pageable pageable
    );

    Set<UUID> findTagIdsByExpenseId(UUID expenseId);

    Expense save(Expense expense);

    void deleteByIdAndUserId(
            UUID expenseId,
            UUID userId
    );
}