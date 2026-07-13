package com.expenseTracker.shrawn.budget.infrastructure.persistence;


import com.expenseTracker.shrawn.budget.domain.BudgetPeriodType;
import com.expenseTracker.shrawn.shared.persistence.AuditableJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "budgets")
public class BudgetJpaEntity extends AuditableJpaEntity {

    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @Column(
            name = "name",
            nullable = false,
            length = 80
    )
    private String name;

    @Column(
            name = "limit_amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal limitAmount;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "period_type",
            nullable = false,
            length = 30
    )
    private BudgetPeriodType periodType;

    @Column(
            name = "start_date",
            nullable = false
    )
    private LocalDate startDate;

    @Column(
            name = "end_date",
            nullable = false
    )
    private LocalDate endDate;

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active;

    protected BudgetJpaEntity() {
        // Required by JPA.
    }

    public BudgetJpaEntity(
            UUID userId,
            String name,
            BigDecimal limitAmount,
            BudgetPeriodType periodType,
            LocalDate startDate,
            LocalDate endDate,
            UUID categoryId,
            boolean active
    ) {
        this.userId = userId;
        this.name = name;
        this.limitAmount = limitAmount;
        this.periodType = periodType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryId = categoryId;
        this.active = active;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public BudgetPeriodType getPeriodType() {
        return periodType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public boolean isActive() {
        return active;
    }

    public void update(
            String name,
            BigDecimal limitAmount,
            BudgetPeriodType periodType,
            LocalDate startDate,
            LocalDate endDate,
            UUID categoryId,
            boolean active
    ) {
        this.name = name;
        this.limitAmount = limitAmount;
        this.periodType = periodType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryId = categoryId;
        this.active = active;
    }
}