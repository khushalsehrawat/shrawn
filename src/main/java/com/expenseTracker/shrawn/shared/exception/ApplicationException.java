package com.expenseTracker.shrawn.shared.exception;

import java.util.Objects;

public abstract class ApplicationException extends RuntimeException {

    private final ErrorCode errorCode;

    protected ApplicationException(
            ErrorCode errorCode,
            String message
    ) {
        super(requireMessage(message));
        this.errorCode = Objects.requireNonNull(
                errorCode,
                "Error code must not be null"
        );
    }

    protected ApplicationException(
            ErrorCode errorCode,
            String message,
            Throwable cause
    ) {
        super(requireMessage(message), cause);
        this.errorCode = Objects.requireNonNull(
                errorCode,
                "Error code must not be null"
        );
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    private static String requireMessage(String message) {
        Objects.requireNonNull(message, "Exception message must not be null");

        if (message.isBlank()) {
            throw new IllegalArgumentException(
                    "Exception message must not be blank"
            );
        }

        return message;
    }
}