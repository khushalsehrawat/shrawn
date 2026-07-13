package com.expenseTracker.shrawn.shared.persistence;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * Base persistence class for all SpendWise JPA entities.
 *
 * <p>This class centralizes the database identifier and optimistic-lock
 * version shared by persistent entities.</p>
 *
 * <p>It is not an entity itself and therefore does not have its own
 * database table. Its mappings are inherited by concrete JPA entities.</p>
 */
@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected BaseJpaEntity() {
        // Required by JPA.
    }

    public UUID getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    public boolean isNew() {
        return id == null;
    }
}