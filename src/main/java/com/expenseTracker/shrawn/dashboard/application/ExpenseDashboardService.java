package com.expenseTracker.shrawn.dashboard.application;

import com.expenseTracker.shrawn.dashboard.domain.ExpenseDashboard;
import com.expenseTracker.shrawn.dashboard.domain.ExpenseDashboardRepository;
import com.expenseTracker.shrawn.dashboard.exception.DashboardNotFoundException;
import com.expenseTracker.shrawn.dashboard.exception.DuplicateDashboardException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ExpenseDashboardService {

    private final ExpenseDashboardRepository dashboardRepository;
    private final Clock clock;

    public ExpenseDashboardService(
            ExpenseDashboardRepository dashboardRepository,
            Clock clock
    ) {
        this.dashboardRepository = dashboardRepository;
        this.clock = clock;
    }

    @Transactional
    public ExpenseDashboard createDashboard(
            UUID userId,
            String name,
            String description
    ) {
        if (dashboardRepository.existsByUserIdAndName(userId, name)) {
            throw new DuplicateDashboardException(name);
        }
        return dashboardRepository.create(userId, name, description);
    }

    @Transactional(readOnly = true)
    public List<ExpenseDashboard> getDashboards(UUID userId) {
        return dashboardRepository.findAllActiveByUserId(userId);
    }

    @Transactional(readOnly = true)
    public ExpenseDashboard getDashboard(
            UUID userId,
            UUID dashboardId
    ) {
        return dashboardRepository.findByIdAndUserId(dashboardId, userId)
                .orElseThrow(() -> new DashboardNotFoundException(dashboardId));
    }

    @Transactional
    public ExpenseDashboard updateDashboard(
            UUID userId,
            UUID dashboardId,
            String name,
            String description
    ) {
        ExpenseDashboard dashboard = getDashboard(userId, dashboardId);
        if (dashboardRepository.existsByUserIdAndNameAndIdNot(userId, name, dashboardId)) {
            throw new DuplicateDashboardException(name);
        }
        dashboard.update(name, description, Instant.now(clock));
        return dashboardRepository.save(dashboard);
    }

    @Transactional
    public ExpenseDashboard deactivateDashboard(
            UUID userId,
            UUID dashboardId
    ) {
        ExpenseDashboard dashboard = getDashboard(userId, dashboardId);
        dashboard.deactivate(Instant.now(clock));
        return dashboardRepository.save(dashboard);
    }
}
