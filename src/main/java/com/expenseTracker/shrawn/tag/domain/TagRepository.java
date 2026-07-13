package com.expenseTracker.shrawn.tag.domain;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository {

    Tag create(
            UUID userId,
            String name
    );

    Optional<Tag> findByIdAndUserId(
            UUID tagId,
            UUID userId
    );

    boolean existsByUserIdAndName(
            UUID userId,
            String name
    );

    boolean existsByUserIdAndNameAndIdNot(
            UUID userId,
            String name,
            UUID excludedTagId
    );

    List<Tag> findAllByUserId(UUID userId);

    List<Tag> findAllActiveByUserId(UUID userId);

    Tag save(Tag tag);
}