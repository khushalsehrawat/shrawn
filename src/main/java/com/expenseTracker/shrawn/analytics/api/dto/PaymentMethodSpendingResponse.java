package com.expenseTracker.shrawn.analytics.api.dto;



import com.expenseTracker.shrawn.analytics.domain.PaymentMethodSpending;
import com.expenseTracker.shrawn.expense.domain.PaymentMethod;

import java.math.BigDecimal;

public record PaymentMethodSpendingResponse(
        PaymentMethod paymentMethod,
        BigDecimal amount,
        long transactionCount,
        BigDecimal percentage
) {

    public static PaymentMethodSpendingResponse from(
            PaymentMethodSpending spending
    ) {
        return new PaymentMethodSpendingResponse(
                spending.paymentMethod(),
                spending.amount().amount(),
                spending.transactionCount(),
                spending.percentage().value()
        );
    }
}