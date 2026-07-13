package com.expenseTracker.shrawn.auth.domain;


import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class RefreshToken {

    private final UUID id;
    private final UUID userId;
    private final String tokenHash;
    private TokenStatus status;
    private final Instant expiresAt;
    private final Instant createdAt;
    private Instant lastUsedAt;

    public RefreshToken(
            UUID id,
            UUID userId,
            String tokenHash,
            TokenStatus status,
            Instant expiresAt,
            Instant createdAt,
            Instant lastUsedAt
    ) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "User ID must not be null");
        this.tokenHash = requireText(tokenHash, "Token hash must not be blank");
        this.status = Objects.requireNonNull(status, "Token status must not be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "Expiry time must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created time must not be null");
        this.lastUsedAt = lastUsedAt;
    }

    public static RefreshToken createNew(
            UUID userId,
            String tokenHash,
            Instant expiresAt,
            Instant now
    ) {
        return new RefreshToken(
                null,
                userId,
                tokenHash,
                TokenStatus.ACTIVE,
                expiresAt,
                now,
                null
        );
    }

    public boolean isExpired(Instant now) {
        Objects.requireNonNull(now, "Current time must not be null");
        return !expiresAt.isAfter(now);
    }

    public boolean isUsable(Instant now) {
        return status.isUsable() && !isExpired(now);
    }

    public void markUsed(Instant now) {
        requireUsable(now);
        this.lastUsedAt = now;
    }

    public void revoke() {
        this.status = TokenStatus.REVOKED;
    }

    private void requireUsable(Instant now) {
        if (!isUsable(now)) {
            throw new IllegalStateException("Refresh token is not usable");
        }
    }

    private static String requireText(
            String value,
            String message
    ) {
        Objects.requireNonNull(value, message);

        String trimmed = value.trim();

        if (trimmed.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return trimmed;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public TokenStatus getStatus() {
        return status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }
}