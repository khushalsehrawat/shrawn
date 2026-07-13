package com.expenseTracker.shrawn.dashboard.api.dto;

import com.expenseTracker.shrawn.dashboard.domain.ExpenseDashboard;

import java.time.Instant;
import java.util.UUID;

public record ExpenseDashboardResponse(
        UUID id,
        String name,
        String description,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    public static ExpenseDashboardResponse from(ExpenseDashboard dashboard) {
        return new ExpenseDashboardResponse(
                dashboard.getId(),
                dashboard.getName(),
                dashboard.getDescription(),
                dashboard.isActive(),
                dashboard.getCreatedAt(),
                dashboard.getUpdatedAt()
        );
    }
}
