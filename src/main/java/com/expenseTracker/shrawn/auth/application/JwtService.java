package com.expenseTracker.shrawn.auth.application;


import com.expenseTracker.shrawn.auth.infrastrucutre.security.JwtTokenProvider;
import com.expenseTracker.shrawn.shared.security.AuthenticatedUser;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AccessToken issueAccessToken(
            AuthenticatedUser authenticatedUser
    ) {
        Objects.requireNonNull(
                authenticatedUser,
                "Authenticated user must not be null"
        );

        String token = jwtTokenProvider.createAccessToken(authenticatedUser);
        Instant expiresAt = jwtTokenProvider.extractExpiration(token);

        return new AccessToken(
                token,
                expiresAt
        );
    }

    public AuthenticatedUser parseAccessToken(String token) {
        return jwtTokenProvider.parseAccessToken(token);
    }

    public record AccessToken(
            String token,
            Instant expiresAt
    ) {
        public AccessToken {
            Objects.requireNonNull(token, "Access token must not be null");
            Objects.requireNonNull(expiresAt, "Access token expiry must not be null");

            if (token.isBlank()) {
                throw new IllegalArgumentException("Access token must not be blank");
            }
        }
    }
}