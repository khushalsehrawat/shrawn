package com.expenseTracker.shrawn.category.infrastructure.persistence;


import com.expenseTracker.shrawn.category.domain.Category;
import com.expenseTracker.shrawn.category.domain.CategoryType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class CategoryPersistenceMapper {

    Category toDomain(CategoryJpaEntity entity) {
        return new Category(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getDescription(),
                entity.getType(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    CategoryJpaEntity toNewEntity(
            UUID userId,
            String name,
            String description,
            CategoryType type
    ) {
        return new CategoryJpaEntity(
                userId,
                Category.validateName(name),
                Category.normalizeDescription(description),
                type,
                true
        );
    }

    void updateEntityFromDomain(
            Category domain,
            CategoryJpaEntity entity
    ) {
        entity.update(
                domain.getName(),
                domain.getDescription(),
                domain.getType(),
                domain.isActive()
        );
    }
}