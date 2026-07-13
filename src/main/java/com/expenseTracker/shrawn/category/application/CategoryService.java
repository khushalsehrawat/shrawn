package com.expenseTracker.shrawn.category.application;


import com.expenseTracker.shrawn.category.domain.Category;
import com.expenseTracker.shrawn.category.domain.CategoryRepository;
import com.expenseTracker.shrawn.category.domain.CategoryType;
import com.expenseTracker.shrawn.category.exception.CategoryNotFoundException;
import com.expenseTracker.shrawn.category.exception.DuplicateCategoryException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final Clock clock;

    public CategoryService(
            CategoryRepository categoryRepository,
            Clock clock
    ) {
        this.categoryRepository = categoryRepository;
        this.clock = clock;
    }

    @Transactional
    public Category createCategory(
            UUID userId,
            String name,
            String description,
            CategoryType type
    ) {
        if (categoryRepository.existsByUserIdAndNameAndType(
                userId,
                name,
                type
        )) {
            throw new DuplicateCategoryException();
        }

        return categoryRepository.create(
                userId,
                name,
                description,
                type
        );
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories(UUID userId) {
        return categoryRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Category> getActiveCategories(UUID userId) {
        return categoryRepository.findAllActiveByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Category getCategory(
            UUID userId,
            UUID categoryId
    ) {
        return categoryRepository.findByIdAndUserId(
                        categoryId,
                        userId
                )
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    @Transactional
    public Category updateCategory(
            UUID userId,
            UUID categoryId,
            String name,
            String description,
            CategoryType type
    ) {
        Category category = getCategory(userId, categoryId);

        if (categoryRepository.existsByUserIdAndNameAndTypeAndIdNot(
                userId,
                name,
                type,
                categoryId
        )) {
            throw new DuplicateCategoryException();
        }

        Instant now = Instant.now(clock);

        category.update(
                name,
                description,
                type,
                now
        );

        return categoryRepository.save(category);
    }

    @Transactional
    public Category deactivateCategory(
            UUID userId,
            UUID categoryId
    ) {
        Category category = getCategory(userId, categoryId);

        Instant now = Instant.now(clock);

        category.deactivate(now);

        return categoryRepository.save(category);
    }

    @Transactional
    public Category reactivateCategory(
            UUID userId,
            UUID categoryId
    ) {
        Category category = getCategory(userId, categoryId);

        Instant now = Instant.now(clock);

        category.reactivate(now);

        return categoryRepository.save(category);
    }
}