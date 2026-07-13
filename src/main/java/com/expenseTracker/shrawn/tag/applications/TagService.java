package com.expenseTracker.shrawn.tag.applications;


import com.expenseTracker.shrawn.tag.domain.Tag;
import com.expenseTracker.shrawn.tag.domain.TagRepository;
import com.expenseTracker.shrawn.tag.exception.DuplicateTagException;
import com.expenseTracker.shrawn.tag.exception.TagNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final Clock clock;

    public TagService(
            TagRepository tagRepository,
            Clock clock
    ) {
        this.tagRepository = tagRepository;
        this.clock = clock;
    }

    @Transactional
    public Tag createTag(
            UUID userId,
            String name
    ) {
        if (tagRepository.existsByUserIdAndName(
                userId,
                name
        )) {
            throw new DuplicateTagException();
        }

        return tagRepository.create(
                userId,
                name
        );
    }

    @Transactional(readOnly = true)
    public List<Tag> getAllTags(UUID userId) {
        return tagRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Tag> getActiveTags(UUID userId) {
        return tagRepository.findAllActiveByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Tag getTag(
            UUID userId,
            UUID tagId
    ) {
        return tagRepository.findByIdAndUserId(
                        tagId,
                        userId
                )
                .orElseThrow(() -> new TagNotFoundException(tagId));
    }

    @Transactional
    public Tag updateTag(
            UUID userId,
            UUID tagId,
            String name
    ) {
        Tag tag = getTag(
                userId,
                tagId
        );

        if (tagRepository.existsByUserIdAndNameAndIdNot(
                userId,
                name,
                tagId
        )) {
            throw new DuplicateTagException();
        }

        Instant now = Instant.now(clock);

        tag.rename(
                name,
                now
        );

        return tagRepository.save(tag);
    }

    @Transactional
    public Tag deactivateTag(
            UUID userId,
            UUID tagId
    ) {
        Tag tag = getTag(
                userId,
                tagId
        );

        Instant now = Instant.now(clock);

        tag.deactivate(now);

        return tagRepository.save(tag);
    }

    @Transactional
    public Tag reactivateTag(
            UUID userId,
            UUID tagId
    ) {
        Tag tag = getTag(
                userId,
                tagId
        );

        Instant now = Instant.now(clock);

        tag.reactivate(now);

        return tagRepository.save(tag);
    }
}