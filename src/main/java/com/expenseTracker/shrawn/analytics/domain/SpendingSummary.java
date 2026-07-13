package com.expenseTracker.shrawn.analytics.domain;



import com.expenseTracker.shrawn.shared.domain.Money;
import com.expenseTracker.shrawn.shared.domain.Percentage;

import java.util.Objects;

public record SpendingSummary(
        Money totalExpense,
        Money totalIncome,
        Money netAmount,
        long transactionCount,
        Percentage expensePercentage
) {

    public SpendingSummary {
        Objects.requireNonNull(totalExpense, "Total expense must not be null");
        Objects.requireNonNull(totalIncome, "Total income must not be null");
        Objects.requireNonNull(netAmount, "Net amount must not be null");
        Objects.requireNonNull(expensePercentage, "Expense percentage must not be null");

        if (transactionCount < 0) {
            throw new IllegalArgumentException("Transaction count must not be negative");
        }
    }
}