package com.expenseTracker.shrawn.dashboard.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataExpenseDashboardRepository extends JpaRepository<ExpenseDashboardJpaEntity, UUID> {

    Optional<ExpenseDashboardJpaEntity> findByIdAndUserId(
            UUID id,
            UUID userId
    );

    List<ExpenseDashboardJpaEntity> findAllByUserIdAndActiveTrueOrderByCreatedAtAsc(UUID userId);

    boolean existsByUserIdAndNameIgnoreCase(
            UUID userId,
            String name
    );

    boolean existsByUserIdAndNameIgnoreCaseAndIdNot(
            UUID userId,
            String name,
            UUID id
    );
}
