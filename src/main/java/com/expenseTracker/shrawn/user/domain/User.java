package com.expenseTracker.shrawn.user.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class User {

    private final UUID id;
    private final EmailAddress email;
    private String fullName;
    private UserStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    public User(
            UUID id,
            EmailAddress email,
            String fullName,
            UserStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "User ID must not be null");
        this.email = Objects.requireNonNull(email, "Email address must not be null");
        this.fullName = validateFullName(fullName);
        this.status = Objects.requireNonNull(status, "User status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created timestamp must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated timestamp must not be null");
    }

    public void verifyEmail(Instant now) {
        requireNotDeleted();

        if (status == UserStatus.ACTIVE) {
            return;
        }

        this.status = UserStatus.ACTIVE;
        this.updatedAt = Objects.requireNonNull(now, "Updated timestamp must not be null");
    }

    public void updateProfile(
            String fullName,
            Instant now
    ) {
        requireNotDeleted();

        this.fullName = validateFullName(fullName);
        this.updatedAt = Objects.requireNonNull(now, "Updated timestamp must not be null");
    }

    public void suspend(Instant now) {
        requireNotDeleted();

        this.status = UserStatus.SUSPENDED;
        this.updatedAt = Objects.requireNonNull(now, "Updated timestamp must not be null");
    }

    public void markDeleted(Instant now) {
        this.status = UserStatus.DELETED;
        this.updatedAt = Objects.requireNonNull(now, "Updated timestamp must not be null");
    }

    public boolean canLogin() {
        return status.canLogin();
    }

    private void requireNotDeleted() {
        if (status.isDeleted()) {
            throw new IllegalStateException("Deleted user cannot be modified");
        }
    }

    private static String validateFullName(String fullName) {
        Objects.requireNonNull(fullName, "Full name must not be null");

        String normalizedFullName = fullName.trim();

        if (normalizedFullName.isBlank()) {
            throw new IllegalArgumentException("Full name must not be blank");
        }

        if (normalizedFullName.length() > 100) {
            throw new IllegalArgumentException("Full name must not exceed 100 characters");
        }

        return normalizedFullName;
    }

    public UUID getId() {
        return id;
    }

    public EmailAddress getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}