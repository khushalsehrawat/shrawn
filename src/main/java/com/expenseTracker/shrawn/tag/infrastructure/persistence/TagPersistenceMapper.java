package com.expenseTracker.shrawn.tag.infrastructure.persistence;


import com.expenseTracker.shrawn.tag.domain.Tag;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class TagPersistenceMapper {

    Tag toDomain(TagJpaEntity entity) {
        return new Tag(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    TagJpaEntity toNewEntity(
            UUID userId,
            String name
    ) {
        return new TagJpaEntity(
                userId,
                Tag.validateName(name),
                true
        );
    }

    void updateEntityFromDomain(
            Tag domain,
            TagJpaEntity entity
    ) {
        entity.update(
                domain.getName(),
                domain.isActive()
        );
    }
}