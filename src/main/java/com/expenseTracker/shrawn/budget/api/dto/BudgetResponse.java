package com.expenseTracker.shrawn.budget.api.dto;



import com.expenseTracker.shrawn.budget.domain.Budget;
import com.expenseTracker.shrawn.budget.domain.BudgetPeriodType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BudgetResponse(
        UUID id,
        String name,
        BigDecimal limitAmount,
        BudgetPeriodType periodType,
        LocalDate startDate,
        LocalDate endDate,
        UUID categoryId,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    public static BudgetResponse from(Budget budget) {
        return new BudgetResponse(
                budget.getId(),
                budget.getName(),
                budget.getLimitAmount().amount(),
                budget.getPeriodType(),
                budget.getPeriod().startDate(),
                budget.getPeriod().endDate(),
                budget.getCategoryId(),
                budget.isActive(),
                budget.getCreatedAt(),
                budget.getUpdatedAt()
        );
    }
}