package com.expenseTracker.shrawn.budget.infrastructure.persistence;


import com.expenseTracker.shrawn.budget.domain.Budget;
import com.expenseTracker.shrawn.budget.domain.BudgetRepository;
import com.expenseTracker.shrawn.budget.exception.BudgetNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class JpaBudgetRepositoryAdapter implements BudgetRepository {

    private final SpringDataBudgetRepository springDataBudgetRepository;
    private final BudgetPersistenceMapper mapper;

    public JpaBudgetRepositoryAdapter(
            SpringDataBudgetRepository springDataBudgetRepository,
            BudgetPersistenceMapper mapper
    ) {
        this.springDataBudgetRepository = springDataBudgetRepository;
        this.mapper = mapper;
    }

    @Override
    public Budget create(Budget budget) {
        BudgetJpaEntity entity = mapper.toNewEntity(budget);
        BudgetJpaEntity savedEntity = springDataBudgetRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Budget> findByIdAndUserId(
            UUID budgetId,
            UUID userId
    ) {
        return springDataBudgetRepository.findByIdAndUserId(
                        budgetId,
                        userId
                )
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsOverlappingBudget(
            UUID userId,
            UUID categoryId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return springDataBudgetRepository.existsOverlappingBudget(
                userId,
                categoryId,
                startDate,
                endDate
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsOverlappingBudgetExcludingId(
            UUID userId,
            UUID categoryId,
            LocalDate startDate,
            LocalDate endDate,
            UUID excludedBudgetId
    ) {
        return springDataBudgetRepository.existsOverlappingBudgetExcludingId(
                userId,
                categoryId,
                startDate,
                endDate,
                excludedBudgetId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Budget> findAllByUserId(UUID userId) {
        return springDataBudgetRepository
                .findAllByUserIdOrderByStartDateDesc(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Budget> findAllActiveByUserId(UUID userId) {
        return springDataBudgetRepository
                .findAllByUserIdAndActiveTrueOrderByStartDateDesc(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Budget> findActiveBudgetsForDate(
            UUID userId,
            LocalDate date
    ) {
        return springDataBudgetRepository.findActiveBudgetsForDate(
                        userId,
                        date
                )
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Budget save(Budget budget) {
        BudgetJpaEntity entity = springDataBudgetRepository
                .findByIdAndUserId(
                        budget.getId(),
                        budget.getUserId()
                )
                .orElseThrow(() -> new BudgetNotFoundException(budget.getId()));

        mapper.updateEntityFromDomain(
                budget,
                entity
        );

        BudgetJpaEntity savedEntity = springDataBudgetRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }
}