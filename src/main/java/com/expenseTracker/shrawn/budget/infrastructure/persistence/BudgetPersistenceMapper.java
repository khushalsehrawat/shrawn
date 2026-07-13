package com.expenseTracker.shrawn.budget.infrastructure.persistence;


import com.expenseTracker.shrawn.budget.domain.Budget;
import com.expenseTracker.shrawn.shared.domain.DateRange;
import com.expenseTracker.shrawn.shared.domain.Money;
import org.springframework.stereotype.Component;

@Component
class BudgetPersistenceMapper {

    Budget toDomain(BudgetJpaEntity entity) {
        return new Budget(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                new Money(entity.getLimitAmount()),
                entity.getPeriodType(),
                new DateRange(
                        entity.getStartDate(),
                        entity.getEndDate()
                ),
                entity.getCategoryId(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    BudgetJpaEntity toNewEntity(Budget budget) {
        return new BudgetJpaEntity(
                budget.getUserId(),
                budget.getName(),
                budget.getLimitAmount().amount(),
                budget.getPeriodType(),
                budget.getPeriod().startDate(),
                budget.getPeriod().endDate(),
                budget.getCategoryId(),
                budget.isActive()
        );
    }

    void updateEntityFromDomain(
            Budget budget,
            BudgetJpaEntity entity
    ) {
        entity.update(
                budget.getName(),
                budget.getLimitAmount().amount(),
                budget.getPeriodType(),
                budget.getPeriod().startDate(),
                budget.getPeriod().endDate(),
                budget.getCategoryId(),
                budget.isActive()
        );
    }
}