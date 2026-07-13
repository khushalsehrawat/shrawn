package com.expenseTracker.shrawn.analytics.infrastructure.persistence;



import com.expenseTracker.shrawn.expense.infrastructure.persistence.ExpenseJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

interface SpringDataAnalyticsRepository
        extends JpaRepository<ExpenseJpaEntity, UUID> {

    @Query("""
            select
                coalesce(sum(case when expense.type = 'EXPENSE' then expense.amount else 0 end), 0),
                coalesce(sum(case when expense.type = 'INCOME' then expense.amount else 0 end), 0),
                count(expense.id)
             from ExpenseJpaEntity expense
             where expense.userId = :userId
               and (:dashboardId is null or expense.dashboardId = :dashboardId)
               and expense.expenseDate >= :startDate
               and expense.expenseDate <= :endDate
            """)
    Object[] getSummaryRaw(
            UUID userId,
            UUID dashboardId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
            select
                category.id,
                category.name,
                coalesce(sum(expense.amount), 0),
                count(expense.id)
              from ExpenseJpaEntity expense
              join CategoryJpaEntity category
                on category.id = expense.categoryId
             where expense.userId = :userId
               and (:dashboardId is null or expense.dashboardId = :dashboardId)
               and expense.type = 'EXPENSE'
               and expense.expenseDate >= :startDate
               and expense.expenseDate <= :endDate
             group by category.id, category.name
             order by sum(expense.amount) desc
            """)
    List<Object[]> getSpendingByCategoryRaw(
            UUID userId,
            UUID dashboardId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
            select
                expense.paymentMethod,
                coalesce(sum(expense.amount), 0),
                count(expense.id)
             from ExpenseJpaEntity expense
             where expense.userId = :userId
               and (:dashboardId is null or expense.dashboardId = :dashboardId)
               and expense.type = 'EXPENSE'
               and expense.expenseDate >= :startDate
               and expense.expenseDate <= :endDate
             group by expense.paymentMethod
             order by sum(expense.amount) desc
            """)
    List<Object[]> getSpendingByPaymentMethodRaw(
            UUID userId,
            UUID dashboardId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
            select
                tag.id,
                tag.name,
                coalesce(sum(expense.amount), 0),
                count(distinct expense.id)
              from ExpenseJpaEntity expense
              join ExpenseTagJpaEntity expenseTag
                on expenseTag.expenseId = expense.id
              join TagJpaEntity tag
                on tag.id = expenseTag.tagId
             where expense.userId = :userId
               and (:dashboardId is null or expense.dashboardId = :dashboardId)
               and expense.type = 'EXPENSE'
               and expense.expenseDate >= :startDate
               and expense.expenseDate <= :endDate
             group by tag.id, tag.name
             order by sum(expense.amount) desc
            """)
    List<Object[]> getSpendingByTagRaw(
            UUID userId,
            UUID dashboardId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
            select
                expense.expenseDate,
                coalesce(sum(expense.amount), 0),
                count(expense.id)
             from ExpenseJpaEntity expense
             where expense.userId = :userId
               and (:dashboardId is null or expense.dashboardId = :dashboardId)
               and expense.type = 'EXPENSE'
               and expense.expenseDate >= :startDate
               and expense.expenseDate <= :endDate
             group by expense.expenseDate
             order by expense.expenseDate asc
            """)
    List<Object[]> getDailySpendingRaw(
            UUID userId,
            UUID dashboardId,
            LocalDate startDate,
            LocalDate endDate
    );
}
