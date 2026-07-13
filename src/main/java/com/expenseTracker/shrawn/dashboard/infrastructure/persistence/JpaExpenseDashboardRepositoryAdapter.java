package com.expenseTracker.shrawn.dashboard.infrastructure.persistence;

import com.expenseTracker.shrawn.dashboard.domain.ExpenseDashboard;
import com.expenseTracker.shrawn.dashboard.domain.ExpenseDashboardRepository;
import com.expenseTracker.shrawn.dashboard.exception.DashboardNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class JpaExpenseDashboardRepositoryAdapter implements ExpenseDashboardRepository {

    private final SpringDataExpenseDashboardRepository springDataExpenseDashboardRepository;
    private final ExpenseDashboardPersistenceMapper mapper;

    public JpaExpenseDashboardRepositoryAdapter(
            SpringDataExpenseDashboardRepository springDataExpenseDashboardRepository,
            ExpenseDashboardPersistenceMapper mapper
    ) {
        this.springDataExpenseDashboardRepository = springDataExpenseDashboardRepository;
        this.mapper = mapper;
    }

    @Override
    public ExpenseDashboard create(
            UUID userId,
            String name,
            String description
    ) {
        ExpenseDashboardJpaEntity entity = mapper.toNewEntity(userId, name, description);
        return mapper.toDomain(springDataExpenseDashboardRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExpenseDashboard> findByIdAndUserId(
            UUID dashboardId,
            UUID userId
    ) {
        return springDataExpenseDashboardRepository.findByIdAndUserId(dashboardId, userId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDashboard> findAllActiveByUserId(UUID userId) {
        return springDataExpenseDashboardRepository.findAllByUserIdAndActiveTrueOrderByCreatedAtAsc(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndName(
            UUID userId,
            String name
    ) {
        return springDataExpenseDashboardRepository.existsByUserIdAndNameIgnoreCase(
                userId,
                ExpenseDashboard.validateName(name)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndNameAndIdNot(
            UUID userId,
            String name,
            UUID excludedDashboardId
    ) {
        return springDataExpenseDashboardRepository.existsByUserIdAndNameIgnoreCaseAndIdNot(
                userId,
                ExpenseDashboard.validateName(name),
                excludedDashboardId
        );
    }

    @Override
    public ExpenseDashboard save(ExpenseDashboard dashboard) {
        ExpenseDashboardJpaEntity entity = springDataExpenseDashboardRepository
                .findByIdAndUserId(dashboard.getId(), dashboard.getUserId())
                .orElseThrow(() -> new DashboardNotFoundException(dashboard.getId()));

        mapper.updateEntityFromDomain(dashboard, entity);
        return mapper.toDomain(springDataExpenseDashboardRepository.save(entity));
    }
}
