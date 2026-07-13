package com.expenseTracker.shrawn.category.domain;



import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

    Category create(
            UUID userId,
            String name,
            String description,
            CategoryType type
    );

    Optional<Category> findByIdAndUserId(
            UUID categoryId,
            UUID userId
    );

    boolean existsByUserIdAndNameAndType(
            UUID userId,
            String name,
            CategoryType type
    );

    boolean existsByUserIdAndNameAndTypeAndIdNot(
            UUID userId,
            String name,
            CategoryType type,
            UUID excludedCategoryId
    );

    List<Category> findAllByUserId(UUID userId);

    List<Category> findAllActiveByUserId(UUID userId);

    Category save(Category category);
}