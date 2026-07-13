package com.expenseTracker.shrawn.analytics.domain;


import com.expenseTracker.shrawn.shared.domain.Money;

import java.time.LocalDate;
import java.util.Objects;

public record DailySpending(
        LocalDate date,
        Money amount,
        long transactionCount
) {

    public DailySpending {
        Objects.requireNonNull(date, "Date must not be null");
        Objects.requireNonNull(amount, "Amount must not be null");

        if (transactionCount < 0) {
            throw new IllegalArgumentException("Transaction count must not be negative");
        }
    }
}