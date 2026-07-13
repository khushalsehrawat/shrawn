package com.expenseTracker.shrawn.shared.api;



import org.springframework.data.domain.Sort;

public enum SortDirection {

    ASC,
    DESC;

    public Sort.Direction toSpringDirection() {
        return this == ASC
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
    }

    public static SortDirection from(
            String value,
            SortDirection defaultDirection
    ) {
        if (value == null || value.isBlank()) {
            return defaultDirection;
        }

        return SortDirection.valueOf(
                value.trim().toUpperCase()
        );
    }
}
