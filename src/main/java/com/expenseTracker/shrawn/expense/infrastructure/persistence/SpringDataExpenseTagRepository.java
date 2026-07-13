package com.expenseTracker.shrawn.expense.infrastructure.persistence;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface SpringDataExpenseTagRepository
        extends JpaRepository<ExpenseTagJpaEntity, UUID> {

    List<ExpenseTagJpaEntity> findAllByExpenseId(UUID expenseId);

    void deleteAllByExpenseId(UUID expenseId);
}