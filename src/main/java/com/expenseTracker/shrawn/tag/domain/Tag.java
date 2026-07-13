package com.expenseTracker.shrawn.tag.domain;


import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Tag {

    private final UUID id;
    private final UUID userId;
    private String name;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    public Tag(
            UUID id,
            UUID userId,
            String name,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "Tag ID must not be null");
        this.userId = Objects.requireNonNull(userId, "User ID must not be null");
        this.name = validateName(name);
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "Created timestamp must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated timestamp must not be null");
    }

    public void rename(
            String name,
            Instant now
    ) {
        requireActive();

        this.name = validateName(name);
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
            throw new IllegalStateException("Inactive tag cannot be modified");
        }
    }

    public static String validateName(String name) {
        Objects.requireNonNull(name, "Tag name must not be null");

        String normalizedName = name.trim();

        if (normalizedName.isBlank()) {
            throw new IllegalArgumentException("Tag name must not be blank");
        }

        if (normalizedName.length() > 40) {
            throw new IllegalArgumentException("Tag name must not exceed 40 characters");
        }

        return normalizedName;
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