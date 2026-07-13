package com.expenseTracker.shrawn.analytics.domain;


import com.expenseTracker.shrawn.shared.domain.Money;
import com.expenseTracker.shrawn.shared.domain.Percentage;

import java.util.Objects;
import java.util.UUID;

public record TagSpending(
        UUID tagId,
        String tagName,
        Money amount,
        long transactionCount,
        Percentage percentage
) {

    public TagSpending {
        Objects.requireNonNull(tagId, "Tag ID must not be null");
        Objects.requireNonNull(tagName, "Tag name must not be null");
        Objects.requireNonNull(amount, "Amount must not be null");
        Objects.requireNonNull(percentage, "Percentage must not be null");

        if (tagName.isBlank()) {
            throw new IllegalArgumentException("Tag name must not be blank");
        }

        if (transactionCount < 0) {
            throw new IllegalArgumentException("Transaction count must not be negative");
        }
    }
}