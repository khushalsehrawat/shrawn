package com.expenseTracker.shrawn.budget.infrastructure.persistence;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataBudgetRepository
        extends JpaRepository<BudgetJpaEntity, UUID> {

    Optional<BudgetJpaEntity> findByIdAndUserId(
            UUID id,
            UUID userId
    );

    List<BudgetJpaEntity> findAllByUserIdOrderByStartDateDesc(
            UUID userId
    );

    List<BudgetJpaEntity> findAllByUserIdAndActiveTrueOrderByStartDateDesc(
            UUID userId
    );

    @Query("""
            select budget
              from BudgetJpaEntity budget
             where budget.userId = :userId
               and budget.active = true
               and budget.startDate <= :date
               and budget.endDate >= :date
             order by budget.startDate desc
            """)
    List<BudgetJpaEntity> findActiveBudgetsForDate(
            UUID userId,
            LocalDate date
    );

    @Query("""
            select count(budget) > 0
              from BudgetJpaEntity budget
             where budget.userId = :userId
               and budget.active = true
               and (
                    (:categoryId is null and budget.categoryId is null)
                    or budget.categoryId = :categoryId
               )
               and budget.startDate <= :endDate
               and budget.endDate >= :startDate
            """)
    boolean existsOverlappingBudget(
            UUID userId,
            UUID categoryId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
            select count(budget) > 0
              from BudgetJpaEntity budget
             where budget.userId = :userId
               and budget.id <> :excludedBudgetId
               and budget.active = true
               and (
                    (:categoryId is null and budget.categoryId is null)
                    or budget.categoryId = :categoryId
               )
               and budget.startDate <= :endDate
               and budget.endDate >= :startDate
            """)
    boolean existsOverlappingBudgetExcludingId(
            UUID userId,
            UUID categoryId,
            LocalDate startDate,
            LocalDate endDate,
            UUID excludedBudgetId
    );
}