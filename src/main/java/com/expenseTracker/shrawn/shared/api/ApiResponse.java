package com.expenseTracker.shrawn.shared.api;

import java.time.Instant;
import java.util.Objects;

public record ApiResponse<T>(
        Instant timestamp,
        boolean success,
        String message,
        T data
) {

    public ApiResponse {

        Objects.requireNonNull(timestamp, "Timestamp must not be null");
        Objects.requireNonNull(message, "Message must not be null");

        if (message.isBlank()) {
            throw new IllegalArgumentException("Message must not be blank");
        }
    }

    public static <T> ApiResponse<T> success(
            String message,
            T data
    ) {
        return new ApiResponse<>(
                Instant.now(),
                true,
                message,
                data
        );
    }

    public static ApiResponse<Void> success(
            String message
    ) {
        return new ApiResponse<>(
                Instant.now(),
                true,
                message,
                null
        );
    }
}
