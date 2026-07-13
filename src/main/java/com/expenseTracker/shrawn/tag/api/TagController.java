package com.expenseTracker.shrawn.tag.api;


import com.expenseTracker.shrawn.shared.api.ApiResponse;
import com.expenseTracker.shrawn.shared.security.AuthenticatedUser;
import com.expenseTracker.shrawn.shared.security.CurrentUser;
import com.expenseTracker.shrawn.tag.api.dto.CreateTagRequest;
import com.expenseTracker.shrawn.tag.api.dto.TagResponse;
import com.expenseTracker.shrawn.tag.api.dto.UpdateTagRequest;
import com.expenseTracker.shrawn.tag.applications.TagService;
import com.expenseTracker.shrawn.tag.domain.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TagResponse> createTag(
            @CurrentUser AuthenticatedUser currentUser,
            @Valid @RequestBody CreateTagRequest request
    ) {
        Tag tag = tagService.createTag(
                currentUser.userId(),
                request.name()
        );

        return ApiResponse.success(
                "Tag created successfully",
                TagResponse.from(tag)
        );
    }

    @GetMapping
    public ApiResponse<List<TagResponse>> getTags(
            @CurrentUser AuthenticatedUser currentUser,
            @RequestParam(defaultValue = "false") boolean activeOnly
    ) {
        List<Tag> tags = activeOnly
                ? tagService.getActiveTags(currentUser.userId())
                : tagService.getAllTags(currentUser.userId());

        List<TagResponse> response = tags.stream()
                .map(TagResponse::from)
                .toList();

        return ApiResponse.success(
                "Tags fetched successfully",
                response
        );
    }

    @GetMapping("/{tagId}")
    public ApiResponse<TagResponse> getTag(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID tagId
    ) {
        Tag tag = tagService.getTag(
                currentUser.userId(),
                tagId
        );

        return ApiResponse.success(
                "Tag fetched successfully",
                TagResponse.from(tag)
        );
    }

    @PutMapping("/{tagId}")
    public ApiResponse<TagResponse> updateTag(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID tagId,
            @Valid @RequestBody UpdateTagRequest request
    ) {
        Tag tag = tagService.updateTag(
                currentUser.userId(),
                tagId,
                request.name()
        );

        return ApiResponse.success(
                "Tag updated successfully",
                TagResponse.from(tag)
        );
    }

    @PatchMapping("/{tagId}/deactivate")
    public ApiResponse<TagResponse> deactivateTag(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID tagId
    ) {
        Tag tag = tagService.deactivateTag(
                currentUser.userId(),
                tagId
        );

        return ApiResponse.success(
                "Tag deactivated successfully",
                TagResponse.from(tag)
        );
    }

    @PatchMapping("/{tagId}/reactivate")
    public ApiResponse<TagResponse> reactivateTag(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID tagId
    ) {
        Tag tag = tagService.reactivateTag(
                currentUser.userId(),
                tagId
        );

        return ApiResponse.success(
                "Tag reactivated successfully",
                TagResponse.from(tag)
        );
    }
}