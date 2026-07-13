package com.expenseTracker.shrawn.expense.infrastructure.persistence;


import com.expenseTracker.shrawn.expense.domain.ExpenseType;
import com.expenseTracker.shrawn.expense.domain.PaymentMethod;
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
@Table(name = "expenses")
public class ExpenseJpaEntity extends AuditableJpaEntity {

    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @Column(
            name = "dashboard_id",
            nullable = false
    )
    private UUID dashboardId;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Column(
            name = "description",
            nullable = false,
            length = 255
    )
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "type",
            nullable = false,
            length = 30
    )
    private ExpenseType type;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_method",
            nullable = false,
            length = 40
    )
    private PaymentMethod paymentMethod;

    @Column(
            name = "expense_date",
            nullable = false
    )
    private LocalDate expenseDate;

    @Column(
            name = "category_id",
            nullable = false
    )
    private UUID categoryId;

    protected ExpenseJpaEntity() {
        // Required by JPA.
    }

    public ExpenseJpaEntity(
            UUID userId,
            UUID dashboardId,
            BigDecimal amount,
            String description,
            ExpenseType type,
            PaymentMethod paymentMethod,
            LocalDate expenseDate,
            UUID categoryId
    ) {
        this.userId = userId;
        this.dashboardId = dashboardId;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.expenseDate = expenseDate;
        this.categoryId = categoryId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getDashboardId() {
        return dashboardId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public ExpenseType getType() {
        return type;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void update(
            UUID dashboardId,
            BigDecimal amount,
            String description,
            ExpenseType type,
            PaymentMethod paymentMethod,
            LocalDate expenseDate,
            UUID categoryId
    ) {
        this.dashboardId = dashboardId;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.expenseDate = expenseDate;
        this.categoryId = categoryId;
    }
}
