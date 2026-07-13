package com.expenseTracker.shrawn.budget.api.dto;


import com.expenseTracker.shrawn.budget.domain.BudgetPeriodType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

public record CreateBudgetRequest(

        @NotBlank(message = "Budget name must not be blank")
        @Size(max = 80, message = "Budget name must not exceed 80 characters")
        String name,

        @NotNull(message = "Budget limit amount must not be null")
        @DecimalMin(value = "0.01", message = "Budget limit amount must be greater than zero")
        BigDecimal limitAmount,

        @NotNull(message = "Budget period type must not be null")
        BudgetPeriodType periodType,

        YearMonth month,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,

        UUID categoryId
) {
}
