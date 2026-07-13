package com.expenseTracker.shrawn.expense.domain;



import com.expenseTracker.shrawn.shared.domain.Money;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Expense {

    private final UUID id;
    private final UUID userId;
    private UUID dashboardId;
    private Money amount;
    private String description;
    private ExpenseType type;
    private PaymentMethod paymentMethod;
    private LocalDate expenseDate;
    private UUID categoryId;
    private Set<UUID> tagIds;
    private final Instant createdAt;
    private Instant updatedAt;

    public Expense(
            UUID id,
            UUID userId,
            UUID dashboardId,
            Money amount,
            String description,
            ExpenseType type,
            PaymentMethod paymentMethod,
            LocalDate expenseDate,
            UUID categoryId,
            Set<UUID> tagIds,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "User ID must not be null");
        this.dashboardId = Objects.requireNonNull(dashboardId, "Dashboard ID must not be null");
        this.amount = Objects.requireNonNull(amount, "Amount must not be null");
        this.description = validateDescription(description);
        this.type = Objects.requireNonNull(type, "Expense type must not be null");
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "Payment method must not be null");
        this.expenseDate = Objects.requireNonNull(expenseDate, "Expense date must not be null");
        this.categoryId = Objects.requireNonNull(categoryId, "Category ID must not be null");
        this.tagIds = normalizeTagIds(tagIds);
        this.createdAt = Objects.requireNonNull(createdAt, "Created timestamp must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated timestamp must not be null");
    }

    public void update(
            UUID dashboardId,
            Money amount,
            String description,
            ExpenseType type,
            PaymentMethod paymentMethod,
            LocalDate expenseDate,
            UUID categoryId,
            Set<UUID> tagIds,
            Instant now
    ) {
        this.dashboardId = Objects.requireNonNull(dashboardId, "Dashboard ID must not be null");
        this.amount = Objects.requireNonNull(amount, "Amount must not be null");
        this.description = validateDescription(description);
        this.type = Objects.requireNonNull(type, "Expense type must not be null");
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "Payment method must not be null");
        this.expenseDate = Objects.requireNonNull(expenseDate, "Expense date must not be null");
        this.categoryId = Objects.requireNonNull(categoryId, "Category ID must not be null");
        this.tagIds = normalizeTagIds(tagIds);
        this.updatedAt = Objects.requireNonNull(now, "Updated timestamp must not be null");
    }

    public static String validateDescription(String description) {
        Objects.requireNonNull(description, "Description must not be null");

        String normalizedDescription = description.trim();

        if (normalizedDescription.isBlank()) {
            throw new IllegalArgumentException("Description must not be blank");
        }

        if (normalizedDescription.length() > 255) {
            throw new IllegalArgumentException("Description must not exceed 255 characters");
        }

        return normalizedDescription;
    }

    public static Set<UUID> normalizeTagIds(Set<UUID> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Set.of();
        }

        if (tagIds.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Tag IDs must not contain null values");
        }

        if (tagIds.size() > 10) {
            throw new IllegalArgumentException("Expense cannot have more than 10 tags");
        }

        return Set.copyOf(tagIds);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getDashboardId() {
        return dashboardId;
    }

    public Money getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public ExpenseType getType() {
        return type;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public Set<UUID> getTagIds() {
        return tagIds;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
