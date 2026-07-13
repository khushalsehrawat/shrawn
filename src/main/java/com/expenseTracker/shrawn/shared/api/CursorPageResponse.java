package com.expenseTracker.shrawn.shared.api;


import java.util.List;
import java.util.Objects;

public record CursorPageResponse<T>(
        List<T> content,
        String nextCursor,
        boolean hasNext,
        int size
) {

    public CursorPageResponse {
        Objects.requireNonNull(content, "Cursor page content must not be null");
        content = List.copyOf(content);

        if (size < 0) {
            throw new IllegalArgumentException("Size must not be negative");
        }

        if (!hasNext && nextCursor != null) {
            throw new IllegalArgumentException(
                    "Next cursor must be null when there is no next page"
            );
        }

        if (hasNext && (nextCursor == null || nextCursor.isBlank())) {
            throw new IllegalArgumentException(
                    "Next cursor must be present when there is a next page"
            );
        }
    }

    public static <T> CursorPageResponse<T> of(
            List<T> content,
            String nextCursor,
            boolean hasNext
    ) {
        Objects.requireNonNull(content, "Content must not be null");

        return new CursorPageResponse<>(
                content,
                nextCursor,
                hasNext,
                content.size()
        );
    }

    public static <T> CursorPageResponse<T> lastPage(
            List<T> content
    ) {
        Objects.requireNonNull(content, "Content must not be null");

        return new CursorPageResponse<>(
                content,
                null,
                false,
                content.size()
        );
    }
}
