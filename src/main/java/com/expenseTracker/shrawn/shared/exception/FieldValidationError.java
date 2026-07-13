package com.expenseTracker.shrawn.shared.exception;

import java.util.Objects;

public record FieldValidationError(
        String field,
        String message
) {

    public FieldValidationError {
        Objects.requireNonNull(field, "Field name must not be null");
        Objects.requireNonNull(message, "Validation message must not be null");

        if (field.isBlank()) {
            throw new IllegalArgumentException(
                    "Field name must not be blank"
            );
        }

        if (message.isBlank()) {
            throw new IllegalArgumentException(
                    "Validation message must not be blank"
            );
        }
    }
}