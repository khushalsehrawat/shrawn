package com.expenseTracker.shrawn.tag.infrastructure.persistence;


import com.expenseTracker.shrawn.shared.persistence.AuditableJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "tags")
public class TagJpaEntity extends AuditableJpaEntity {

    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @Column(
            name = "name",
            nullable = false,
            length = 40
    )
    private String name;

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active;

    protected TagJpaEntity() {
        // Required by JPA.
    }

    public TagJpaEntity(
            UUID userId,
            String name,
            boolean active
    ) {
        this.userId = userId;
        this.name = name;
        this.active = active;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public void update(
            String name,
            boolean active
    ) {
        this.name = name;
        this.active = active;
    }
}