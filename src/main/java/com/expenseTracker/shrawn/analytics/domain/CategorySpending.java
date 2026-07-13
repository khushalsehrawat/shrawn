package com.expenseTracker.shrawn.analytics.domain;


import com.expenseTracker.shrawn.shared.domain.Money;
import com.expenseTracker.shrawn.shared.domain.Percentage;

import java.util.Objects;
import java.util.UUID;

public record CategorySpending(
        UUID categoryId,
        String categoryName,
        Money amount,
        long transactionCount,
        Percentage percentage
) {

    public CategorySpending {
        Objects.requireNonNull(categoryId, "Category ID must not be null");
        Objects.requireNonNull(categoryName, "Category name must not be null");
        Objects.requireNonNull(amount, "Amount must not be null");
        Objects.requireNonNull(percentage, "Percentage must not be null");

        if (categoryName.isBlank()) {
            throw new IllegalArgumentException("Category name must not be blank");
        }

        if (transactionCount < 0) {
            throw new IllegalArgumentException("Transaction count must not be negative");
        }
    }
}