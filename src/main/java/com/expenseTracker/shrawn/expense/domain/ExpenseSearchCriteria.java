package com.expenseTracker.shrawn.expense.domain;


import com.expenseTracker.shrawn.shared.domain.DateRange;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record ExpenseSearchCriteria(
        Optional<UUID> dashboardId,
        Optional<DateRange> dateRange,
        Optional<UUID> categoryId,
        Optional<UUID> tagId,
        Optional<ExpenseType> type,
        Optional<PaymentMethod> paymentMethod,
        Optional<String> keyword
) {

    public ExpenseSearchCriteria {
        dashboardId = normalize(dashboardId);
        dateRange = normalize(dateRange);
        categoryId = normalize(categoryId);
        tagId = normalize(tagId);
        type = normalize(type);
        paymentMethod = normalize(paymentMethod);
        keyword = normalizeKeyword(keyword);
    }

    public static ExpenseSearchCriteria empty() {
        return new ExpenseSearchCriteria(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }

    private static <T> Optional<T> normalize(Optional<T> optional) {
        return optional == null ? Optional.empty() : optional;
    }

    private static Optional<String> normalizeKeyword(Optional<String> keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return Optional.empty();
        }

        String normalizedKeyword = Objects.requireNonNull(keyword.get())
                .trim();

        if (normalizedKeyword.isBlank()) {
            return Optional.empty();
        }

        if (normalizedKeyword.length() > 100) {
            throw new IllegalArgumentException("Keyword must not exceed 100 characters");
        }

        return Optional.of(normalizedKeyword);
    }
}
