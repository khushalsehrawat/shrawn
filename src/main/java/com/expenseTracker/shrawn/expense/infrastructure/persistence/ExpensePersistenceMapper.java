package com.expenseTracker.shrawn.expense.infrastructure.persistence;


import com.expenseTracker.shrawn.expense.domain.Expense;
import com.expenseTracker.shrawn.shared.domain.Money;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
class ExpensePersistenceMapper {

    Expense toDomain(
            ExpenseJpaEntity entity,
            Set<UUID> tagIds
    ) {
        return new Expense(
                entity.getId(),
                entity.getUserId(),
                entity.getDashboardId(),
                new Money(entity.getAmount()),
                entity.getDescription(),
                entity.getType(),
                entity.getPaymentMethod(),
                entity.getExpenseDate(),
                entity.getCategoryId(),
                tagIds,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    ExpenseJpaEntity toNewEntity(Expense expense) {
        return new ExpenseJpaEntity(
                expense.getUserId(),
                expense.getDashboardId(),
                expense.getAmount().amount(),
                expense.getDescription(),
                expense.getType(),
                expense.getPaymentMethod(),
                expense.getExpenseDate(),
                expense.getCategoryId()
        );
    }

    void updateEntityFromDomain(
            Expense expense,
            ExpenseJpaEntity entity
    ) {
        entity.update(
                expense.getDashboardId(),
                expense.getAmount().amount(),
                expense.getDescription(),
                expense.getType(),
                expense.getPaymentMethod(),
                expense.getExpenseDate(),
                expense.getCategoryId()
        );
    }
}
