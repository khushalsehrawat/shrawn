package com.expenseTracker.shrawn.analytics.api.dto;



import com.expenseTracker.shrawn.analytics.domain.CategorySpending;

import java.math.BigDecimal;
import java.util.UUID;

public record CategorySpendingResponse(
        UUID categoryId,
        String categoryName,
        BigDecimal amount,
        long transactionCount,
        BigDecimal percentage
) {

    public static CategorySpendingResponse from(CategorySpending spending) {
        return new CategorySpendingResponse(
                spending.categoryId(),
                spending.categoryName(),
                spending.amount().amount(),
                spending.transactionCount(),
                spending.percentage().value()
        );
    }
}