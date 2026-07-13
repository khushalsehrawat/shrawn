package com.expenseTracker.shrawn.analytics.api.dto;



import com.expenseTracker.shrawn.analytics.domain.DailySpending;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailySpendingResponse(
        LocalDate date,
        BigDecimal amount,
        long transactionCount
) {

    public static DailySpendingResponse from(DailySpending spending) {
        return new DailySpendingResponse(
                spending.date(),
                spending.amount().amount(),
                spending.transactionCount()
        );
    }
}