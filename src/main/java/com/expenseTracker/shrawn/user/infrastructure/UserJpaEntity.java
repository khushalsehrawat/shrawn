package com.expenseTracker.shrawn.user.infrastructure;

import com.expenseTracker.shrawn.shared.persistence.AuditableJpaEntity;
import com.expenseTracker.shrawn.user.domain.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserJpaEntity extends AuditableJpaEntity {

    @Column(
            name = "email",
            nullable = false,
            unique = true,
            length = 254
    )
    private String email;


    @Column(
            name = "password_hash",
            nullable = false,
            length = 100
    )
    private String passwordHash;


    @Column(
            name = "full_name",
            nullable = false,
            length = 100
    )
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 50
    )
    private UserStatus status;

    protected UserJpaEntity() {
        // Required by JPA.
    }

    public UserJpaEntity(
            String email,
            String fullName,
            String passwordHash,
            UserStatus status
    ) {
        this.email = email;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void updateProfile(String fullName) {
        this.fullName = fullName;
    }

    public void changeStatus(UserStatus status) {
        this.status = status;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}