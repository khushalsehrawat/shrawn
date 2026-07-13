package com.expenseTracker.shrawn.budget.domain;


import com.expenseTracker.shrawn.shared.domain.DateRange;
import com.expenseTracker.shrawn.shared.domain.Money;

import java.time.Instant;
import java.time.YearMonth;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Budget {

    private final UUID id;
    private final UUID userId;
    private String name;
    private Money limitAmount;
    private BudgetPeriodType periodType;
    private DateRange period;
    private UUID categoryId;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    public Budget(
            UUID id,
            UUID userId,
            String name,
            Money limitAmount,
            BudgetPeriodType periodType,
            DateRange period,
            UUID categoryId,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "User ID must not be null");
        this.name = validateName(name);
        this.limitAmount = validateLimitAmount(limitAmount);
        this.periodType = Objects.requireNonNull(periodType, "Budget period type must not be null");
        this.period = Objects.requireNonNull(period, "Budget period must not be null");
        this.categoryId = categoryId;
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "Created timestamp must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated timestamp must not be null");
    }

    public static DateRange monthlyPeriod(YearMonth yearMonth) {
        Objects.requireNonNull(yearMonth, "Year month must not be null");

        return new DateRange(
                yearMonth.atDay(1),
                yearMonth.atEndOfMonth()
        );
    }

    public boolean isOverallBudget() {
        return categoryId == null;
    }

    public Optional<UUID> getCategoryIdOptional() {
        return Optional.ofNullable(categoryId);
    }

    public void update(
            String name,
            Money limitAmount,
            BudgetPeriodType periodType,
            DateRange period,
            UUID categoryId,
            Instant now
    ) {
        requireActive();

        this.name = validateName(name);
        this.limitAmount = validateLimitAmount(limitAmount);
        this.periodType = Objects.requireNonNull(periodType, "Budget period type must not be null");
        this.period = Objects.requireNonNull(period, "Budget period must not be null");
        this.categoryId = categoryId;
        this.updatedAt = Objects.requireNonNull(now, "Updated timestamp must not be null");
    }

    public void deactivate(Instant now) {
        if (!active) {
            return;
        }

        this.active = false;
        this.updatedAt = Objects.requireNonNull(now, "Updated timestamp must not be null");
    }

    public void reactivate(Instant now) {
        if (active) {
            return;
        }

        this.active = true;
        this.updatedAt = Objects.requireNonNull(now, "Updated timestamp must not be null");
    }

    private void requireActive() {
        if (!active) {
            throw new IllegalStateException("Inactive budget cannot be modified");
        }
    }

    public static String validateName(String name) {
        Objects.requireNonNull(name, "Budget name must not be null");

        String normalizedName = name.trim();

        if (normalizedName.isBlank()) {
            throw new IllegalArgumentException("Budget name must not be blank");
        }

        if (normalizedName.length() > 80) {
            throw new IllegalArgumentException("Budget name must not exceed 80 characters");
        }

        return normalizedName;
    }

    public static Money validateLimitAmount(Money limitAmount) {
        Objects.requireNonNull(limitAmount, "Budget limit amount must not be null");

        if (limitAmount.isZero()) {
            throw new IllegalArgumentException("Budget limit amount must be greater than zero");
        }

        return limitAmount;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public Money getLimitAmount() {
        return limitAmount;
    }

    public BudgetPeriodType getPeriodType() {
        return periodType;
    }

    public DateRange getPeriod() {
        return period;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}