package com.expenseTracker.shrawn.shared.exception;

import java.util.Objects;

public class ResourceNotFoundException extends ApplicationException {

    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }

    public ResourceNotFoundException(
            String resourceName,
            Object resourceId
    ) {
        super(
                ErrorCode.RESOURCE_NOT_FOUND,
                buildMessage(resourceName, resourceId)
        );
    }

    private static String buildMessage(
            String resourceName,
            Object resourceId
    ) {
        Objects.requireNonNull(
                resourceName,
                "Resource name must not be null"
        );
        Objects.requireNonNull(
                resourceId,
                "Resource ID must not be null"
        );

        if (resourceName.isBlank()) {
            throw new IllegalArgumentException(
                    "Resource name must not be blank"
            );
        }

        return resourceName + " not found with ID: " + resourceId;
    }
}
