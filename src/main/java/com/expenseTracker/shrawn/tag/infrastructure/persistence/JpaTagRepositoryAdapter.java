package com.expenseTracker.shrawn.tag.infrastructure.persistence;


import com.expenseTracker.shrawn.tag.domain.Tag;
import com.expenseTracker.shrawn.tag.domain.TagRepository;
import com.expenseTracker.shrawn.tag.exception.TagNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class JpaTagRepositoryAdapter implements TagRepository {

    private final SpringDataTagRepository springDataTagRepository;
    private final TagPersistenceMapper mapper;

    public JpaTagRepositoryAdapter(
            SpringDataTagRepository springDataTagRepository,
            TagPersistenceMapper mapper
    ) {
        this.springDataTagRepository = springDataTagRepository;
        this.mapper = mapper;
    }

    @Override
    public Tag create(
            UUID userId,
            String name
    ) {
        TagJpaEntity entity = mapper.toNewEntity(
                userId,
                name
        );

        TagJpaEntity savedEntity =
                springDataTagRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tag> findByIdAndUserId(
            UUID tagId,
            UUID userId
    ) {
        return springDataTagRepository.findByIdAndUserId(
                        tagId,
                        userId
                )
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndName(
            UUID userId,
            String name
    ) {
        return springDataTagRepository.existsByUserIdAndNameIgnoreCase(
                userId,
                Tag.validateName(name)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndNameAndIdNot(
            UUID userId,
            String name,
            UUID excludedTagId
    ) {
        return springDataTagRepository.existsByUserIdAndNameIgnoreCaseAndIdNot(
                userId,
                Tag.validateName(name),
                excludedTagId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findAllByUserId(UUID userId) {
        return springDataTagRepository.findAllByUserIdOrderByNameAsc(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findAllActiveByUserId(UUID userId) {
        return springDataTagRepository.findAllByUserIdAndActiveTrueOrderByNameAsc(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Tag save(Tag tag) {
        TagJpaEntity entity = springDataTagRepository.findByIdAndUserId(
                        tag.getId(),
                        tag.getUserId()
                )
                .orElseThrow(() -> new TagNotFoundException(tag.getId()));

        mapper.updateEntityFromDomain(tag, entity);

        TagJpaEntity savedEntity =
                springDataTagRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }
}