package com.expenseTracker.shrawn.expense.api.dto;



import com.expenseTracker.shrawn.expense.domain.Expense;
import com.expenseTracker.shrawn.expense.domain.ExpenseType;
import com.expenseTracker.shrawn.expense.domain.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        BigDecimal amount,
        String description,
        ExpenseType type,
        PaymentMethod paymentMethod,
        LocalDate expenseDate,
        UUID dashboardId,
        UUID categoryId,
        Set<UUID> tagIds,
        Instant createdAt,
        Instant updatedAt
) {

    public static ExpenseResponse from(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount().amount(),
                expense.getDescription(),
                expense.getType(),
                expense.getPaymentMethod(),
                expense.getExpenseDate(),
                expense.getDashboardId(),
                expense.getCategoryId(),
                expense.getTagIds(),
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }
}
