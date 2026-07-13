package com.expenseTracker.shrawn.dashboard.infrastructure.persistence;

import com.expenseTracker.shrawn.dashboard.domain.ExpenseDashboard;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class ExpenseDashboardPersistenceMapper {

    ExpenseDashboard toDomain(ExpenseDashboardJpaEntity entity) {
        return new ExpenseDashboard(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    ExpenseDashboardJpaEntity toNewEntity(
            UUID userId,
            String name,
            String description
    ) {
        return new ExpenseDashboardJpaEntity(
                userId,
                ExpenseDashboard.validateName(name),
                ExpenseDashboard.normalizeDescription(description),
                true
        );
    }

    void updateEntityFromDomain(
            ExpenseDashboard dashboard,
            ExpenseDashboardJpaEntity entity
    ) {
        entity.update(
                dashboard.getName(),
                dashboard.getDescription(),
                dashboard.isActive()
        );
    }
}
