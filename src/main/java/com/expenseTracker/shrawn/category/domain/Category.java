package com.expenseTracker.shrawn.category.domain;


import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Category {

    private final UUID id;
    private final UUID userId;
    private String name;
    private String description;
    private CategoryType type;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    public Category(
            UUID id,
            UUID userId,
            String name,
            String description,
            CategoryType type,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "Category ID must not be null");
        this.userId = Objects.requireNonNull(userId, "User ID must not be null");
        this.name = validateName(name);
        this.description = normalizeDescription(description);
        this.type = Objects.requireNonNull(type, "Category type must not be null");
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "Created timestamp must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated timestamp must not be null");
    }

    public void update(
            String name,
            String description,
            CategoryType type,
            Instant now
    ) {
        requireActive();

        this.name = validateName(name);
        this.description = normalizeDescription(description);
        this.type = Objects.requireNonNull(type, "Category type must not be null");
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
            throw new IllegalStateException("Inactive category cannot be modified");
        }
    }

    public static String validateName(String name) {
        Objects.requireNonNull(name, "Category name must not be null");

        String normalizedName = name.trim();

        if (normalizedName.isBlank()) {
            throw new IllegalArgumentException("Category name must not be blank");
        }

        if (normalizedName.length() > 60) {
            throw new IllegalArgumentException("Category name must not exceed 60 characters");
        }

        return normalizedName;
    }

    public static String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }

        String normalizedDescription = description.trim();

        if (normalizedDescription.isBlank()) {
            return null;
        }

        if (normalizedDescription.length() > 255) {
            throw new IllegalArgumentException("Category description must not exceed 255 characters");
        }

        return normalizedDescription;
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

    public String getDescription() {
        return description;
    }

    public CategoryType getType() {
        return type;
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