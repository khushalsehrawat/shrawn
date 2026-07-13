package com.expenseTracker.shrawn.dashboard.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseDashboardRepository {

    ExpenseDashboard create(
            UUID userId,
            String name,
            String description
    );

    Optional<ExpenseDashboard> findByIdAndUserId(
            UUID dashboardId,
            UUID userId
    );

    List<ExpenseDashboard> findAllActiveByUserId(UUID userId);

    boolean existsByUserIdAndName(
            UUID userId,
            String name
    );

    boolean existsByUserIdAndNameAndIdNot(
            UUID userId,
            String name,
            UUID excludedDashboardId
    );

    ExpenseDashboard save(ExpenseDashboard dashboard);
}
