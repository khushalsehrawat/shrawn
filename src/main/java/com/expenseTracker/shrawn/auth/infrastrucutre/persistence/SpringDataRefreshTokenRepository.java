package com.expenseTracker.shrawn.auth.infrastrucutre.persistence;


import com.expenseTracker.shrawn.auth.domain.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;


interface SpringDataRefreshTokenRepository
        extends JpaRepository<RefreshTokenJpaEntity, UUID> {

    Optional<RefreshTokenJpaEntity> findByTokenHash(String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update RefreshTokenJpaEntity token
               set token.status = :newStatus
             where token.userId = :userId
               and token.status = :currentStatus
            """)
    int updateStatusByUserIdAndStatus(
            UUID userId,
            TokenStatus currentStatus,
            TokenStatus newStatus
    );
}