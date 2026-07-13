package com.expenseTracker.shrawn.auth.application;


import com.expenseTracker.shrawn.auth.api.dto.AuthResponse;
import com.expenseTracker.shrawn.auth.exception.InvalidCredentialsException;
import com.expenseTracker.shrawn.shared.security.AuthenticatedUser;
import com.expenseTracker.shrawn.user.domain.EmailAddress;
import com.expenseTracker.shrawn.user.domain.User;
import com.expenseTracker.shrawn.user.domain.UserAccountCredentials;
import com.expenseTracker.shrawn.user.domain.UserRepository;
import com.expenseTracker.shrawn.user.exception.EmailAlreadyExistsException;
import com.expenseTracker.shrawn.user.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthenticationService {

    private static final String TOKEN_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordService passwordService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponse register(
            String email,
            String fullName,
            String password
    ) {
        EmailAddress emailAddress = new EmailAddress(email);

        if (userRepository.existsByEmail(emailAddress)) {
            throw new EmailAlreadyExistsException();
        }

        String passwordHash = passwordService.hash(password);

        User user = userRepository.create(
                emailAddress,
                fullName,
                passwordHash
        );

        return issueAuthResponse(
                user.getId(),
                user.getEmail().value()
        );
    }

    @Transactional
    public AuthResponse login(
            String email,
            String password
    ) {
        EmailAddress emailAddress = new EmailAddress(email);

        UserAccountCredentials credentials = userRepository
                .findCredentialsByEmail(emailAddress)
                .orElseThrow(InvalidCredentialsException::new);

        if (!credentials.canLogin()) {
            throw new InvalidCredentialsException();
        }

        boolean passwordMatches = passwordService.matches(
                password,
                credentials.passwordHash()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        return issueAuthResponse(
                credentials.userId(),
                credentials.email().value()
        );
    }

    @Transactional
    public AuthResponse refresh(String refreshToken) {
        RefreshTokenService.RefreshTokenValidationResult refreshResult =
                refreshTokenService.validateAndRotate(refreshToken);

        User user = userRepository.findById(refreshResult.userId())
                .orElseThrow(() -> new UserNotFoundException(refreshResult.userId()));

        if (!user.canLogin()) {
            refreshTokenService.revokeAllForUser(user.getId());
            throw new InvalidCredentialsException();
        }

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                user.getId(),
                user.getEmail().value()
        );

        JwtService.AccessToken accessToken =
                jwtService.issueAccessToken(authenticatedUser);

        return new AuthResponse(
                TOKEN_TYPE,
                accessToken.token(),
                accessToken.expiresAt(),
                refreshResult.newRefreshToken(),
                refreshResult.newRefreshTokenExpiresAt(),
                user.getId(),
                user.getEmail().value()
        );
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    private AuthResponse issueAuthResponse(
            UUID userId,
            String email
    ) {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                userId,
                email
        );

        JwtService.AccessToken accessToken =
                jwtService.issueAccessToken(authenticatedUser);

        RefreshTokenService.RefreshTokenResult refreshToken =
                refreshTokenService.issueRefreshToken(userId);

        return new AuthResponse(
                TOKEN_TYPE,
                accessToken.token(),
                accessToken.expiresAt(),
                refreshToken.token(),
                refreshToken.expiresAt(),
                userId,
                email
        );
    }
}