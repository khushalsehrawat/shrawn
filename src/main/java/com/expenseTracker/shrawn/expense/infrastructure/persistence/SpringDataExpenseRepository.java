package com.expenseTracker.shrawn.expense.infrastructure.persistence;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

interface SpringDataExpenseRepository
        extends JpaRepository<ExpenseJpaEntity, UUID> {

    Optional<ExpenseJpaEntity> findByIdAndUserId(
            UUID id,
            UUID userId
    );

    void deleteByIdAndUserId(
            UUID id,
            UUID userId
    );

    @Query(
            value = """
                    select distinct e.*
                      from expenses e
                     left join expense_tags et
                        on et.expense_id = e.id
                     where e.user_id = :userId
                       and (cast(:dashboardId as uuid) is null or e.dashboard_id = :dashboardId)
                       and (cast(:startDate as date) is null or e.expense_date >= :startDate)
                       and (cast(:endDate as date) is null or e.expense_date <= :endDate)
                       and (cast(:categoryId as uuid) is null or e.category_id = :categoryId)
                       and (cast(:tagId as uuid) is null or et.tag_id = :tagId)
                       and (cast(:type as varchar) is null or e.type = :type)
                       and (cast(:paymentMethod as varchar) is null or e.payment_method = :paymentMethod)
                       and (
                            cast(:keyword as varchar) is null
                            or lower(e.description) like lower(concat('%', cast(:keyword as varchar), '%'))
                       )
                     order by e.expense_date desc, e.created_at desc
                    """,
            countQuery = """
                    select count(distinct e.id)
                      from expenses e
                     left join expense_tags et
                        on et.expense_id = e.id
                     where e.user_id = :userId
                       and (cast(:dashboardId as uuid) is null or e.dashboard_id = :dashboardId)
                       and (cast(:startDate as date) is null or e.expense_date >= :startDate)
                       and (cast(:endDate as date) is null or e.expense_date <= :endDate)
                       and (cast(:categoryId as uuid) is null or e.category_id = :categoryId)
                       and (cast(:tagId as uuid) is null or et.tag_id = :tagId)
                       and (cast(:type as varchar) is null or e.type = :type)
                       and (cast(:paymentMethod as varchar) is null or e.payment_method = :paymentMethod)
                       and (
                            cast(:keyword as varchar) is null
                            or lower(e.description) like lower(concat('%', cast(:keyword as varchar), '%'))
                       )
                    """,
            nativeQuery = true
    )
    Page<ExpenseJpaEntity> search(
            UUID userId,
            UUID dashboardId,
            LocalDate startDate,
            LocalDate endDate,
            UUID categoryId,
            UUID tagId,
            String type,
            String paymentMethod,
            String keyword,
            Pageable pageable
    );
}
