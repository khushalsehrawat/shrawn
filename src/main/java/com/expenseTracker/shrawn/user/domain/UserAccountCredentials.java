package com.expenseTracker.shrawn.user.domain;


import java.util.Objects;
import java.util.UUID;

public record UserAccountCredentials(
        UUID userId,
        EmailAddress email,
        String passwordHash,
        UserStatus status
) {

    public UserAccountCredentials {
        Objects.requireNonNull(userId, "User ID must not be null");
        Objects.requireNonNull(email, "Email address must not be null");
        Objects.requireNonNull(passwordHash, "Password hash must not be null");
        Objects.requireNonNull(status, "User status must not be null");

        if (passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash must not be blank");
        }
    }

    public boolean canLogin() {
        return status.canLogin();
    }
}