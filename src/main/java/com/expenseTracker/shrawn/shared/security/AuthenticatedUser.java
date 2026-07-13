package com.expenseTracker.shrawn.shared.security;


import java.util.Objects;
import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        String email
) {

    public AuthenticatedUser {
        Objects.requireNonNull(userId, "User ID must not be null");
        Objects.requireNonNull(email, "Email must not be null");

        email = email.trim().toLowerCase();

        if (email.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
    }
}