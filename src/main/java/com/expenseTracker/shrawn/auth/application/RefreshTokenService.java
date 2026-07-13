package com.expenseTracker.shrawn.auth.application;


import com.expenseTracker.shrawn.auth.config.JwtProperties;
import com.expenseTracker.shrawn.auth.domain.RefreshToken;
import com.expenseTracker.shrawn.auth.domain.RefreshTokenRepository;
import com.expenseTracker.shrawn.auth.exception.InvalidTokenException;
import com.expenseTracker.shrawn.auth.exception.TokenExpiredException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final int REFRESH_TOKEN_BYTES = 64;

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHashService tokenHashService;
    private final JwtProperties jwtProperties;
    private final Clock clock;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            TokenHashService tokenHashService,
            JwtProperties jwtProperties,
            Clock clock
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenHashService = tokenHashService;
        this.jwtProperties = jwtProperties;
        this.clock = clock;
    }

    @Transactional
    public RefreshTokenResult issueRefreshToken(UUID userId) {
        Objects.requireNonNull(userId, "User ID must not be null");

        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(jwtProperties.getRefreshTokenExpiration());

        String rawToken = generateRawToken();
        String tokenHash = tokenHashService.hash(rawToken);

        RefreshToken refreshToken = RefreshToken.createNew(
                userId,
                tokenHash,
                expiresAt,
                now
        );

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        return new RefreshTokenResult(
                rawToken,
                savedToken.getExpiresAt()
        );
    }

    @Transactional
    public RefreshTokenValidationResult validateAndRotate(
            String rawRefreshToken
    ) {
        String tokenHash = tokenHashService.hash(rawRefreshToken);

        RefreshToken existingToken = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token is invalid"));

        Instant now = Instant.now(clock);

        if (existingToken.isExpired(now)) {
            existingToken.revoke();
            refreshTokenRepository.save(existingToken);

            throw new TokenExpiredException("Refresh token has expired");
        }

        if (!existingToken.isUsable(now)) {
            throw new InvalidTokenException("Refresh token is not usable");
        }

        existingToken.markUsed(now);
        existingToken.revoke();
        refreshTokenRepository.save(existingToken);

        RefreshTokenResult newRefreshToken =
                issueRefreshToken(existingToken.getUserId());

        return new RefreshTokenValidationResult(
                existingToken.getUserId(),
                newRefreshToken.token(),
                newRefreshToken.expiresAt()
        );
    }

    @Transactional
    public void revokeAllForUser(UUID userId) {
        Objects.requireNonNull(userId, "User ID must not be null");
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    private String generateRawToken() {
        byte[] bytes = new byte[REFRESH_TOKEN_BYTES];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    public record RefreshTokenResult(
            String token,
            Instant expiresAt
    ) {
        public RefreshTokenResult {
            Objects.requireNonNull(token, "Refresh token must not be null");
            Objects.requireNonNull(expiresAt, "Refresh token expiry must not be null");

            if (token.isBlank()) {
                throw new IllegalArgumentException("Refresh token must not be blank");
            }
        }
    }

    public record RefreshTokenValidationResult(
            UUID userId,
            String newRefreshToken,
            Instant newRefreshTokenExpiresAt
    ) {
        public RefreshTokenValidationResult {
            Objects.requireNonNull(userId, "User ID must not be null");
            Objects.requireNonNull(
                    newRefreshToken,
                    "New refresh token must not be null"
            );
            Objects.requireNonNull(
                    newRefreshTokenExpiresAt,
                    "New refresh token expiry must not be null"
            );

            if (newRefreshToken.isBlank()) {
                throw new IllegalArgumentException(
                        "New refresh token must not be blank"
                );
            }
        }
    }

    @Transactional
    public void revoke(String rawRefreshToken) {
        String tokenHash = tokenHashService.hash(rawRefreshToken);

        RefreshToken existingToken = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token is invalid"));

        existingToken.revoke();

        refreshTokenRepository.save(existingToken);
    }
}