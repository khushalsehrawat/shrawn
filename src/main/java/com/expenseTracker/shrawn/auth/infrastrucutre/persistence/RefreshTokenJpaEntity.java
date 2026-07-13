package com.expenseTracker.shrawn.auth.infrastrucutre.persistence;


import com.expenseTracker.shrawn.auth.domain.TokenStatus;
import com.expenseTracker.shrawn.shared.persistence.AuditableJpaEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenJpaEntity extends AuditableJpaEntity {

    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @Column(
            name = "token_hash",
            nullable = false,
            unique = true,
            length = 128
    )
    private String tokenHash;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private TokenStatus status;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private Instant expiresAt;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    protected RefreshTokenJpaEntity() {
        // Required by JPA.
    }

    public RefreshTokenJpaEntity(
            UUID userId,
            String tokenHash,
            TokenStatus status,
            Instant expiresAt,
            Instant lastUsedAt
    ) {
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.status = status;
        this.expiresAt = expiresAt;
        this.lastUsedAt = lastUsedAt;
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

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public void changeStatus(TokenStatus status) {
        this.status = status;
    }

    public void markUsed(Instant lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }
}