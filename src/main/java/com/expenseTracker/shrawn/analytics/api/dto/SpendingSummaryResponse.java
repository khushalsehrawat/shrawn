package com.expenseTracker.shrawn.analytics.api.dto;


import com.expenseTracker.shrawn.analytics.domain.SpendingSummary;

import java.math.BigDecimal;

public record SpendingSummaryResponse(
        BigDecimal totalExpense,
        BigDecimal totalIncome,
        BigDecimal netAmount,
        long transactionCount,
        BigDecimal expensePercentage
) {

    public static SpendingSummaryResponse from(SpendingSummary summary) {
        return new SpendingSummaryResponse(
                summary.totalExpense().amount(),
                summary.totalIncome().amount(),
                summary.netAmount().amount(),
                summary.transactionCount(),
                summary.expensePercentage().value()
        );
    }
}