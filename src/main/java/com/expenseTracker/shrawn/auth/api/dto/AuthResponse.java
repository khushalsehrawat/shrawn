package com.expenseTracker.shrawn.auth.api.dto;


import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record AuthResponse(
        String tokenType,
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt,
        UUID userId,
        String email
) {

    public AuthResponse {
        Objects.requireNonNull(tokenType, "Token type must not be null");
        Objects.requireNonNull(accessToken, "Access token must not be null");
        Objects.requireNonNull(accessTokenExpiresAt, "Access token expiry must not be null");
        Objects.requireNonNull(refreshToken, "Refresh token must not be null");
        Objects.requireNonNull(refreshTokenExpiresAt, "Refresh token expiry must not be null");
        Objects.requireNonNull(userId, "User ID must not be null");
        Objects.requireNonNull(email, "Email must not be null");

        if (tokenType.isBlank()) {
            throw new IllegalArgumentException("Token type must not be blank");
        }

        if (accessToken.isBlank()) {
            throw new IllegalArgumentException("Access token must not be blank");
        }

        if (refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token must not be blank");
        }

        if (email.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
    }
}