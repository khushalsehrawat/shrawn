package com.expenseTracker.shrawn.user.infrastructure.persistence;


import com.expenseTracker.shrawn.user.domain.EmailAddress;
import com.expenseTracker.shrawn.user.domain.User;
import com.expenseTracker.shrawn.user.domain.UserAccountCredentials;
import com.expenseTracker.shrawn.user.domain.UserStatus;
import com.expenseTracker.shrawn.user.infrastructure.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
class UserPersistenceMapper {

    User toDomain(UserJpaEntity entity) {
        return new User(
                entity.getId(),
                new EmailAddress(entity.getEmail()),
                entity.getFullName(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    UserJpaEntity toNewEntity(
            EmailAddress email,
            String fullName,
            String passwordHash
    ) {
        return new UserJpaEntity(
                email.value(),
                fullName,
                passwordHash,
                UserStatus.ACTIVE
        );
    }

    void updateEntityFromDomain(
            User domain,
            UserJpaEntity entity
    ) {
        entity.updateProfile(domain.getFullName());
        entity.changeStatus(domain.getStatus());
    }

    public UserAccountCredentials toCredentials(UserJpaEntity entity) {
        return new UserAccountCredentials(
                entity.getId(),
                new EmailAddress(entity.getEmail()),
                entity.getPasswordHash(),
                entity.getStatus()
        );
    }

}