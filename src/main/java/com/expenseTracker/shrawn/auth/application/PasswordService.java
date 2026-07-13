package com.expenseTracker.shrawn.auth.application;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String hash(String rawPassword) {
        validateRawPassword(rawPassword);
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(
            String rawPassword,
            String passwordHash
    ) {
        validateRawPassword(rawPassword);
        Objects.requireNonNull(passwordHash, "Password hash must not be null");

        return passwordEncoder.matches(rawPassword, passwordHash);
    }

    private void validateRawPassword(String rawPassword) {
        Objects.requireNonNull(rawPassword, "Password must not be null");

        if (rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }

        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters");
        }

        if (rawPassword.length() > 72) {
            throw new IllegalArgumentException("Password must not exceed 72 characters");
        }
    }
}