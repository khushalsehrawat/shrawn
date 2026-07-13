package com.expenseTracker.shrawn.auth.application;


import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

@Service
public class TokenHashService {

    private static final String HASH_ALGORITHM = "SHA-512";

    public String hash(String token) {
        Objects.requireNonNull(token, "Token must not be null");

        if (token.isBlank()) {
            throw new IllegalArgumentException("Token must not be blank");
        }

        MessageDigest digest = newDigest();

        byte[] hashedBytes = digest.digest(
                token.getBytes(StandardCharsets.UTF_8)
        );

        return HexFormat.of().formatHex(hashedBytes);
    }

    private MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance(HASH_ALGORITHM);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-512 algorithm is not available",
                    exception
            );
        }
    }
}