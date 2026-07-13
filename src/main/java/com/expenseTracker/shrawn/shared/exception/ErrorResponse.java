package com.expenseTracker.shrawn.shared.exception;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        ErrorCode code,
        String message,
        String path,
        String traceId,
        List<FieldValidationError> fieldErrors
) {

    public ErrorResponse {
        Objects.requireNonNull(timestamp, "Timestamp must not be null");
        Objects.requireNonNull(error, "Error name must not be null");
        Objects.requireNonNull(code, "Error code must not be null");
        Objects.requireNonNull(message, "Error message must not be null");
        Objects.requireNonNull(path, "Request path must not be null");

        fieldErrors = fieldErrors == null
                ? List.of()
                : List.copyOf(fieldErrors);
    }

    public static ErrorResponse of(
            int status,
            String error,
            ErrorCode code,
            String message,
            String path,
            String traceId
    ) {
        return new ErrorResponse(
                Instant.now(),
                status,
                error,
                code,
                message,
                path,
                traceId,
                List.of()
        );
    }

    public static ErrorResponse withFieldErrors(
            int status,
            String error,
            ErrorCode code,
            String message,
            String path,
            String traceId,
            List<FieldValidationError> fieldErrors
    ) {
        return new ErrorResponse(
                Instant.now(),
                status,
                error,
                code,
                message,
                path,
                traceId,
                fieldErrors
        );
    }
}