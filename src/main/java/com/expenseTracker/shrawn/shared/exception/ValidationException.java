package com.expenseTracker.shrawn.shared.exception;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ValidationException extends ApplicationException {

    private final Map<String, String> fieldErrors;

    public ValidationException(String message) {
        this(message, Collections.emptyMap());
    }

    public ValidationException(
            String message,
            Throwable cause
    ) {
        super(ErrorCode.VALIDATION_ERROR, message, cause);
        this.fieldErrors = Collections.emptyMap();
    }

    public ValidationException(
            String message,
            Map<String, String> fieldErrors
    ) {
        super(ErrorCode.VALIDATION_ERROR, message);
        this.fieldErrors = immutableFieldErrors(fieldErrors);
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    private static Map<String, String> immutableFieldErrors(
            Map<String, String> fieldErrors
    ) {
        Objects.requireNonNull(
                fieldErrors,
                "Field errors must not be null"
        );

        Map<String, String> validatedErrors = new LinkedHashMap<>();

        fieldErrors.forEach((field, error) -> {
            if (field == null || field.isBlank()) {
                throw new IllegalArgumentException(
                        "Validation field name must not be blank"
                );
            }

            if (error == null || error.isBlank()) {
                throw new IllegalArgumentException(
                        "Validation error message must not be blank"
                );
            }

            validatedErrors.put(field, error);
        });

        return Collections.unmodifiableMap(validatedErrors);
    }
}