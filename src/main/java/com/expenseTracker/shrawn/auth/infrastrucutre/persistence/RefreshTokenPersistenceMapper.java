package com.expenseTracker.shrawn.auth.infrastrucutre.persistence;

import com.expenseTracker.shrawn.auth.domain.RefreshToken;
import org.springframework.stereotype.Component;

@Component
class RefreshTokenPersistenceMapper {

    RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return new RefreshToken(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getStatus(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getLastUsedAt()
        );
    }

    RefreshTokenJpaEntity toNewEntity(RefreshToken refreshToken) {
        return new RefreshTokenJpaEntity(
                refreshToken.getUserId(),
                refreshToken.getTokenHash(),
                refreshToken.getStatus(),
                refreshToken.getExpiresAt(),
                refreshToken.getLastUsedAt()
        );
    }

    void updateEntityFromDomain(
            RefreshToken domain,
            RefreshTokenJpaEntity entity
    ) {
        entity.changeStatus(domain.getStatus());
        entity.markUsed(domain.getLastUsedAt());
    }
}