package com.expenseTracker.shrawn.analytics.domain;


import com.expenseTracker.shrawn.expense.domain.PaymentMethod;
import com.expenseTracker.shrawn.shared.domain.Money;
import com.expenseTracker.shrawn.shared.domain.Percentage;

import java.util.Objects;

public record PaymentMethodSpending(
        PaymentMethod paymentMethod,
        Money amount,
        long transactionCount,
        Percentage percentage
) {

    public PaymentMethodSpending {
        Objects.requireNonNull(paymentMethod, "Payment method must not be null");
        Objects.requireNonNull(amount, "Amount must not be null");
        Objects.requireNonNull(percentage, "Percentage must not be null");

        if (transactionCount < 0) {
            throw new IllegalArgumentException("Transaction count must not be negative");
        }
    }
}
