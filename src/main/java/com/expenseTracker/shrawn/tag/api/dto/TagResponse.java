package com.expenseTracker.shrawn.tag.api.dto;


import com.expenseTracker.shrawn.tag.domain.Tag;

import java.time.Instant;
import java.util.UUID;

public record TagResponse(
        UUID id,
        String name,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    public static TagResponse from(Tag tag) {
        return new TagResponse(
                tag.getId(),
                tag.getName(),
                tag.isActive(),
                tag.getCreatedAt(),
                tag.getUpdatedAt()
        );
    }
}