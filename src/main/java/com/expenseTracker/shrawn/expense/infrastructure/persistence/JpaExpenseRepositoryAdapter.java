package com.expenseTracker.shrawn.expense.infrastructure.persistence;

import com.expenseTracker.shrawn.expense.domain.Expense;
import com.expenseTracker.shrawn.expense.domain.ExpenseRepository;
import com.expenseTracker.shrawn.expense.domain.ExpenseSearchCriteria;
import com.expenseTracker.shrawn.expense.exception.ExpenseNotFoundException;
import com.expenseTracker.shrawn.shared.domain.DateRange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
@Repository
@Transactional
public class JpaExpenseRepositoryAdapter implements ExpenseRepository {

    private final SpringDataExpenseRepository springDataExpenseRepository;
    private final SpringDataExpenseTagRepository springDataExpenseTagRepository;
    private final ExpensePersistenceMapper mapper;

    public JpaExpenseRepositoryAdapter(
            SpringDataExpenseRepository springDataExpenseRepository,
            SpringDataExpenseTagRepository springDataExpenseTagRepository,
            ExpensePersistenceMapper mapper
    ) {
        this.springDataExpenseRepository = springDataExpenseRepository;
        this.springDataExpenseTagRepository = springDataExpenseTagRepository;
        this.mapper = mapper;
    }

    @Override
    public Expense create(
            UUID userId,
            Expense expense
    ) {
        ExpenseJpaEntity entity = mapper.toNewEntity(expense);
        ExpenseJpaEntity savedEntity = springDataExpenseRepository.save(entity);

        replaceTags(
                savedEntity.getId(),
                expense.getTagIds()
        );

        return mapper.toDomain(
                savedEntity,
                expense.getTagIds()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<Expense> findByIdAndUserId(
            UUID expenseId,
            UUID userId
    ) {
        return springDataExpenseRepository.findByIdAndUserId(
                        expenseId,
                        userId
                )
                .map(entity -> mapper.toDomain(
                        entity,
                        findTagIdsByExpenseId(entity.getId())
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Expense> search(
            UUID userId,
            ExpenseSearchCriteria criteria,
            Pageable pageable
    ) {
        LocalDate startDate = criteria.dateRange()
                .map(DateRange::startDate)
                .orElse(null);

        LocalDate endDate = criteria.dateRange()
                .map(DateRange::endDate)
                .orElse(null);

        return springDataExpenseRepository.search(
                        userId,
                        criteria.dashboardId().orElse(null),
                        startDate,
                        endDate,
                        criteria.categoryId().orElse(null),
                        criteria.tagId().orElse(null),
                        criteria.type().map(Enum::name).orElse(null),
                        criteria.paymentMethod().map(Enum::name).orElse(null),
                        criteria.keyword().orElse(null),
                        pageable
                )
                .map(entity -> mapper.toDomain(
                        entity,
                        findTagIdsByExpenseId(entity.getId())
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UUID> findTagIdsByExpenseId(UUID expenseId) {
        return springDataExpenseTagRepository.findAllByExpenseId(expenseId)
                .stream()
                .map(ExpenseTagJpaEntity::getTagId)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Expense save(Expense expense) {
        ExpenseJpaEntity entity = springDataExpenseRepository
                .findByIdAndUserId(
                        expense.getId(),
                        expense.getUserId()
                )
                .orElseThrow(() -> new ExpenseNotFoundException(expense.getId()));

        mapper.updateEntityFromDomain(
                expense,
                entity
        );

        ExpenseJpaEntity savedEntity = springDataExpenseRepository.save(entity);

        replaceTags(
                expense.getId(),
                expense.getTagIds()
        );

        return mapper.toDomain(
                savedEntity,
                expense.getTagIds()
        );
    }

    @Override
    public void deleteByIdAndUserId(
            UUID expenseId,
            UUID userId
    ) {
        ExpenseJpaEntity entity = springDataExpenseRepository
                .findByIdAndUserId(
                        expenseId,
                        userId
                )
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));

        springDataExpenseTagRepository.deleteAllByExpenseId(entity.getId());
        springDataExpenseRepository.delete(entity);
    }

    private void replaceTags(
            UUID expenseId,
            Set<UUID> tagIds
    ) {
        springDataExpenseTagRepository.deleteAllByExpenseId(expenseId);

        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }

        tagIds.stream()
                .map(tagId -> new ExpenseTagJpaEntity(
                        expenseId,
                        tagId
                ))
                .forEach(springDataExpenseTagRepository::save);
    }
}
