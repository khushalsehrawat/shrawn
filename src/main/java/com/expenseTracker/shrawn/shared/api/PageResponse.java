package com.expenseTracker.shrawn.shared.api;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean empty
) {

    public PageResponse {
        Objects.requireNonNull(content, "Page content must not be null");
        content = List.copyOf(content);

        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be negative");
        }

        if (size < 1) {
            throw new IllegalArgumentException("Page size must be greater than zero");
        }

        if (totalElements < 0) {
            throw new IllegalArgumentException("Total elements must not be negative");
        }

        if (totalPages < 0) {
            throw new IllegalArgumentException("Total pages must not be negative");
        }
    }

    public static <T> PageResponse<T> from(Page<T> page) {
        Objects.requireNonNull(page, "Page must not be null");

        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }

    public static <S, T> PageResponse<T> from(
            Page<S> page,
            Function<S, T> mapper
    ) {
        Objects.requireNonNull(page, "Page must not be null");
        Objects.requireNonNull(mapper, "Mapper must not be null");

        List<T> mappedContent = page.getContent()
                .stream()
                .map(mapper)
                .toList();

        return new PageResponse<>(
                mappedContent,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }
}