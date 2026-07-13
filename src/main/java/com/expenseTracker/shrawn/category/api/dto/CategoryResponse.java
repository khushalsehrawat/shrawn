package com.expenseTracker.shrawn.category.api.dto;



import com.expenseTracker.shrawn.category.domain.Category;
import com.expenseTracker.shrawn.category.domain.CategoryType;

import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String description,
        CategoryType type,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getType(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}