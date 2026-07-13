package com.expenseTracker.shrawn.auth.infrastrucutre.persistence;


import com.expenseTracker.shrawn.auth.domain.RefreshToken;
import com.expenseTracker.shrawn.auth.domain.RefreshTokenRepository;
import com.expenseTracker.shrawn.auth.domain.TokenStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final SpringDataRefreshTokenRepository springDataRefreshTokenRepository;
    private final RefreshTokenPersistenceMapper mapper;

    public JpaRefreshTokenRepositoryAdapter(
            SpringDataRefreshTokenRepository springDataRefreshTokenRepository,
            RefreshTokenPersistenceMapper mapper
    ) {
        this.springDataRefreshTokenRepository = springDataRefreshTokenRepository;
        this.mapper = mapper;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        if (refreshToken.getId() == null) {
            RefreshTokenJpaEntity entity = mapper.toNewEntity(refreshToken);
            RefreshTokenJpaEntity savedEntity =
                    springDataRefreshTokenRepository.save(entity);

            return mapper.toDomain(savedEntity);
        }

        RefreshTokenJpaEntity entity = springDataRefreshTokenRepository
                .findById(refreshToken.getId())
                .orElseThrow(() -> new IllegalStateException("Refresh token does not exist"));

        mapper.updateEntityFromDomain(refreshToken, entity);

        RefreshTokenJpaEntity savedEntity =
                springDataRefreshTokenRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return springDataRefreshTokenRepository.findByTokenHash(tokenHash)
                .map(mapper::toDomain);
    }

    @Override
    public void revokeAllByUserId(UUID userId) {
        springDataRefreshTokenRepository.updateStatusByUserIdAndStatus(
                userId,
                TokenStatus.ACTIVE,
                TokenStatus.REVOKED
        );
    }
}