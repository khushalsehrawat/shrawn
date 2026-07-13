package com.expenseTracker.shrawn.tag.infrastructure.persistence;



import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataTagRepository extends JpaRepository<TagJpaEntity, UUID> {

    Optional<TagJpaEntity> findByIdAndUserId(
            UUID id,
            UUID userId
    );

    boolean existsByUserIdAndNameIgnoreCase(
            UUID userId,
            String name
    );

    boolean existsByUserIdAndNameIgnoreCaseAndIdNot(
            UUID userId,
            String name,
            UUID id
    );

    List<TagJpaEntity> findAllByUserIdOrderByNameAsc(UUID userId);

    List<TagJpaEntity> findAllByUserIdAndActiveTrueOrderByNameAsc(UUID userId);
}