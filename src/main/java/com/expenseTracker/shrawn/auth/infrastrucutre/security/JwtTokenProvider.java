package com.expenseTracker.shrawn.auth.infrastrucutre.security;


import com.expenseTracker.shrawn.auth.config.JwtProperties;
import com.expenseTracker.shrawn.auth.exception.InvalidTokenException;
import com.expenseTracker.shrawn.auth.exception.TokenExpiredException;
import com.expenseTracker.shrawn.shared.security.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private static final String EMAIL_CLAIM = "email";

    private final JwtProperties jwtProperties;
    private final Clock clock;
    private final SecretKey secretKey;

    public JwtTokenProvider(
            JwtProperties jwtProperties,
            Clock clock
    ) {
        this.jwtProperties = jwtProperties;
        this.clock = clock;
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String createAccessToken(AuthenticatedUser authenticatedUser) {
        Objects.requireNonNull(
                authenticatedUser,
                "Authenticated user must not be null"
        );

        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(authenticatedUser.userId().toString())
                .claim(EMAIL_CLAIM, authenticatedUser.email())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public AuthenticatedUser parseAccessToken(String token) {
        Claims claims = parseClaims(token);

        UUID userId = parseUserId(claims.getSubject());
        String email = claims.get(
                EMAIL_CLAIM,
                String.class
        );

        return new AuthenticatedUser(
                userId,
                email
        );
    }

    public Instant extractExpiration(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration().toInstant();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(jwtProperties.getIssuer())
                    .clock(() -> Date.from(Instant.now(clock)))
                    .build()
                    .parseSignedClaims(requireToken(token))
                    .getPayload();
        } catch (ExpiredJwtException exception) {
            throw new TokenExpiredException("Access token has expired");
        } catch (JwtException | IllegalArgumentException exception) {
            throw new InvalidTokenException("Access token is invalid");
        }
    }

    private UUID parseUserId(String subject) {
        try {
            return UUID.fromString(subject);
        } catch (RuntimeException exception) {
            throw new InvalidTokenException("Access token subject is invalid");
        }
    }

    private String requireToken(String token) {
        Objects.requireNonNull(token, "Token must not be null");

        String trimmed = token.trim();

        if (trimmed.isBlank()) {
            throw new InvalidTokenException("Access token must not be blank");
        }

        return trimmed;
    }
}