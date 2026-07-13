package com.expenseTracker.shrawn.category.infrastructure.persistence;


import com.expenseTracker.shrawn.category.domain.CategoryType;
import com.expenseTracker.shrawn.shared.persistence.AuditableJpaEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "categories")
public class CategoryJpaEntity extends AuditableJpaEntity {

    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @Column(
            name = "name",
            nullable = false,
            length = 60
    )
    private String name;

    @Column(
            name = "description",
            length = 255
    )
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "type",
            nullable = false,
            length = 30
    )
    private CategoryType type;

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active;

    protected CategoryJpaEntity() {
        // Required by JPA.
    }

    public CategoryJpaEntity(
            UUID userId,
            String name,
            String description,
            CategoryType type,
            boolean active
    ) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.type = type;
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

    public CategoryType getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public void update(
            String name,
            String description,
            CategoryType type,
            boolean active
    ) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.active = active;
    }
}