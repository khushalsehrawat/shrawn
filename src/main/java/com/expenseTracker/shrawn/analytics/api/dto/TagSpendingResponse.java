package com.expenseTracker.shrawn.analytics.api.dto;


import com.expenseTracker.shrawn.analytics.domain.TagSpending;

import java.math.BigDecimal;
import java.util.UUID;

public record TagSpendingResponse(
        UUID tagId,
        String tagName,
        BigDecimal amount,
        long transactionCount,
        BigDecimal percentage
) {

    public static TagSpendingResponse from(TagSpending spending) {
        return new TagSpendingResponse(
                spending.tagId(),
                spending.tagName(),
                spending.amount().amount(),
                spending.transactionCount(),
                spending.percentage().value()
        );
    }
}