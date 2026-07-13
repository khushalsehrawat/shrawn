package com.expenseTracker.shrawn.expense.infrastructure.persistence;


import com.expenseTracker.shrawn.shared.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "expense_tags")
public class ExpenseTagJpaEntity extends BaseJpaEntity {

    @Column(
            name = "expense_id",
            nullable = false
    )
    private UUID expenseId;

    @Column(
            name = "tag_id",
            nullable = false
    )
    private UUID tagId;

    protected ExpenseTagJpaEntity() {
        // Required by JPA.
    }

    public ExpenseTagJpaEntity(
            UUID expenseId,
            UUID tagId
    ) {
        this.expenseId = expenseId;
        this.tagId = tagId;
    }

    public UUID getExpenseId() {
        return expenseId;
    }

    public UUID getTagId() {
        return tagId;
    }
}