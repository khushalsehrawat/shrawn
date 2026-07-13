package com.expenseTracker.shrawn.expense.api.dto;


import com.expenseTracker.shrawn.expense.domain.ExpenseType;
import com.expenseTracker.shrawn.expense.domain.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record CreateExpenseRequest(

        @NotNull(message = "Amount must not be null")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Description must not be blank")
        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description,

        @NotNull(message = "Expense type must not be null")
        ExpenseType type,

        @NotNull(message = "Payment method must not be null")
        PaymentMethod paymentMethod,

        @NotNull(message = "Expense date must not be null")
        LocalDate expenseDate,

        UUID dashboardId,

        @NotNull(message = "Category ID must not be null")
        UUID categoryId,

        @Size(max = 10, message = "Expense cannot have more than 10 tags")
        Set<UUID> tagIds
) {
}
