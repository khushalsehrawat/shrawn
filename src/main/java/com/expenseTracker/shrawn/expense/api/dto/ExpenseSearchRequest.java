package com.expenseTracker.shrawn.expense.api.dto;


import com.expenseTracker.shrawn.expense.domain.ExpenseSearchCriteria;
import com.expenseTracker.shrawn.expense.domain.ExpenseType;
import com.expenseTracker.shrawn.expense.domain.PaymentMethod;
import com.expenseTracker.shrawn.shared.domain.DateRange;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record ExpenseSearchRequest(
        UUID dashboardId,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,

        UUID categoryId,

        UUID tagId,

        ExpenseType type,

        PaymentMethod paymentMethod,

        String keyword
) {

    public ExpenseSearchCriteria toCriteria() {
        Optional<DateRange> dateRange = buildDateRange();

        return new ExpenseSearchCriteria(
                Optional.ofNullable(dashboardId),
                dateRange,
                Optional.ofNullable(categoryId),
                Optional.ofNullable(tagId),
                Optional.ofNullable(type),
                Optional.ofNullable(paymentMethod),
                Optional.ofNullable(keyword)
        );
    }

    private Optional<DateRange> buildDateRange() {
        if (startDate == null && endDate == null) {
            return Optional.empty();
        }

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Both startDate and endDate must be provided together"
            );
        }

        return Optional.of(new DateRange(
                startDate,
                endDate
        ));
    }
}
