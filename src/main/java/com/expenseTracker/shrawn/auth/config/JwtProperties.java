package com.expenseTracker.shrawn.auth.config;


import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Component
@ConfigurationProperties(prefix = "shrawn.security.jwt")
public class JwtProperties {

    private String issuer;
    private String secret;
    private Duration accessTokenExpiration;
    private Duration refreshTokenExpiration;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Duration getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(Duration accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public Duration getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(Duration refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @PostConstruct
    void validate() {
        issuer = requireText(issuer, "JWT issuer must not be blank");
        secret = requireText(secret, "JWT secret must not be blank");

        Objects.requireNonNull(
                accessTokenExpiration,
                "Access token expiration must not be null"
        );
        Objects.requireNonNull(
                refreshTokenExpiration,
                "Refresh token expiration must not be null"
        );

        if (secret.length() < 64) {
            throw new IllegalStateException(
                    "JWT secret must contain at least 64 characters for HS512"
            );
        }

        if (accessTokenExpiration.isNegative()
                || accessTokenExpiration.isZero()) {
            throw new IllegalStateException(
                    "Access token expiration must be positive"
            );
        }

        if (refreshTokenExpiration.isNegative()
                || refreshTokenExpiration.isZero()) {
            throw new IllegalStateException(
                    "Refresh token expiration must be positive"
            );
        }

        if (refreshTokenExpiration.compareTo(accessTokenExpiration) <= 0) {
            throw new IllegalStateException(
                    "Refresh token expiration must be longer than access token expiration"
            );
        }
    }

    private String requireText(
            String value,
            String message
    ) {
        Objects.requireNonNull(value, message);

        String trimmed = value.trim();

        if (trimmed.isBlank()) {
            throw new IllegalStateException(message);
        }

        return trimmed;
    }
}