package com.expenseTracker.shrawn.dashboard.infrastructure.persistence;

import com.expenseTracker.shrawn.shared.persistence.AuditableJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "expense_dashboards")
public class ExpenseDashboardJpaEntity extends AuditableJpaEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected ExpenseDashboardJpaEntity() {
        // Required by JPA.
    }

    public ExpenseDashboardJpaEntity(
            UUID userId,
            String name,
            String description,
            boolean active
    ) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public void update(
            String name,
            String description,
            boolean active
    ) {
        this.name = name;
        this.description = description;
        this.active = active;
    }
}
