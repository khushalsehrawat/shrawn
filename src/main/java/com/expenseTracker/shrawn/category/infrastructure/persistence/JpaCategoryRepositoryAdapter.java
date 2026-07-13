package com.expenseTracker.shrawn.category.infrastructure.persistence;


import com.expenseTracker.shrawn.category.domain.Category;
import com.expenseTracker.shrawn.category.domain.CategoryRepository;
import com.expenseTracker.shrawn.category.domain.CategoryType;
import com.expenseTracker.shrawn.category.exception.CategoryNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class JpaCategoryRepositoryAdapter implements CategoryRepository {

    private final SpringDataCategoryRepository springDataCategoryRepository;
    private final CategoryPersistenceMapper mapper;

    public JpaCategoryRepositoryAdapter(
            SpringDataCategoryRepository springDataCategoryRepository,
            CategoryPersistenceMapper mapper
    ) {
        this.springDataCategoryRepository = springDataCategoryRepository;
        this.mapper = mapper;
    }

    @Override
    public Category create(
            UUID userId,
            String name,
            String description,
            CategoryType type
    ) {
        CategoryJpaEntity entity = mapper.toNewEntity(
                userId,
                name,
                description,
                type
        );

        CategoryJpaEntity savedEntity =
                springDataCategoryRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findByIdAndUserId(
            UUID categoryId,
            UUID userId
    ) {
        return springDataCategoryRepository.findByIdAndUserId(
                        categoryId,
                        userId
                )
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndNameAndType(
            UUID userId,
            String name,
            CategoryType type
    ) {
        return springDataCategoryRepository
                .existsByUserIdAndNameIgnoreCaseAndType(
                        userId,
                        Category.validateName(name),
                        type
                );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndNameAndTypeAndIdNot(
            UUID userId,
            String name,
            CategoryType type,
            UUID excludedCategoryId
    ) {
        return springDataCategoryRepository
                .existsByUserIdAndNameIgnoreCaseAndTypeAndIdNot(
                        userId,
                        Category.validateName(name),
                        type,
                        excludedCategoryId
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAllByUserId(UUID userId) {
        return springDataCategoryRepository
                .findAllByUserIdOrderByNameAsc(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAllActiveByUserId(UUID userId) {
        return springDataCategoryRepository
                .findAllByUserIdAndActiveTrueOrderByNameAsc(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Category save(Category category) {
        CategoryJpaEntity entity = springDataCategoryRepository
                .findByIdAndUserId(
                        category.getId(),
                        category.getUserId()
                )
                .orElseThrow(() -> new CategoryNotFoundException(category.getId()));

        mapper.updateEntityFromDomain(category, entity);

        CategoryJpaEntity savedEntity =
                springDataCategoryRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }
}