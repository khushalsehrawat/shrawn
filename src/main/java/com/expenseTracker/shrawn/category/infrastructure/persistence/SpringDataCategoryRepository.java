package com.expenseTracker.shrawn.category.infrastructure.persistence;


import com.expenseTracker.shrawn.category.domain.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataCategoryRepository
        extends JpaRepository<CategoryJpaEntity, UUID> {

    Optional<CategoryJpaEntity> findByIdAndUserId(
            UUID id,
            UUID userId
    );

    boolean existsByUserIdAndNameIgnoreCaseAndType(
            UUID userId,
            String name,
            CategoryType type
    );

    boolean existsByUserIdAndNameIgnoreCaseAndTypeAndIdNot(
            UUID userId,
            String name,
            CategoryType type,
            UUID id
    );

    List<CategoryJpaEntity> findAllByUserIdOrderByNameAsc(UUID userId);

    List<CategoryJpaEntity> findAllByUserIdAndActiveTrueOrderByNameAsc(UUID userId);
}