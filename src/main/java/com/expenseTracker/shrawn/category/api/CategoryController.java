package com.expenseTracker.shrawn.category.api;



import com.expenseTracker.shrawn.category.api.dto.CategoryResponse;
import com.expenseTracker.shrawn.category.api.dto.CreateCategoryRequest;
import com.expenseTracker.shrawn.category.api.dto.UpdateCategoryRequest;
import com.expenseTracker.shrawn.category.application.CategoryService;
import com.expenseTracker.shrawn.category.domain.Category;
import com.expenseTracker.shrawn.shared.api.ApiResponse;
import com.expenseTracker.shrawn.shared.security.AuthenticatedUser;
import com.expenseTracker.shrawn.shared.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryResponse> createCategory(
            @CurrentUser AuthenticatedUser currentUser,
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        Category category = categoryService.createCategory(
                currentUser.userId(),
                request.name(),
                request.description(),
                request.type()
        );

        return ApiResponse.success(
                "Category created successfully",
                CategoryResponse.from(category)
        );
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getCategories(
            @CurrentUser AuthenticatedUser currentUser,
            @RequestParam(defaultValue = "false") boolean activeOnly
    ) {
        List<Category> categories = activeOnly
                ? categoryService.getActiveCategories(currentUser.userId())
                : categoryService.getAllCategories(currentUser.userId());

        List<CategoryResponse> response = categories.stream()
                .map(CategoryResponse::from)
                .toList();

        return ApiResponse.success(
                "Categories fetched successfully",
                response
        );
    }

    @GetMapping("/{categoryId}")
    public ApiResponse<CategoryResponse> getCategory(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID categoryId
    ) {
        Category category = categoryService.getCategory(
                currentUser.userId(),
                categoryId
        );

        return ApiResponse.success(
                "Category fetched successfully",
                CategoryResponse.from(category)
        );
    }

    @PutMapping("/{categoryId}")
    public ApiResponse<CategoryResponse> updateCategory(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID categoryId,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        Category category = categoryService.updateCategory(
                currentUser.userId(),
                categoryId,
                request.name(),
                request.description(),
                request.type()
        );

        return ApiResponse.success(
                "Category updated successfully",
                CategoryResponse.from(category)
        );
    }

    @PatchMapping("/{categoryId}/deactivate")
    public ApiResponse<CategoryResponse> deactivateCategory(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID categoryId
    ) {
        Category category = categoryService.deactivateCategory(
                currentUser.userId(),
                categoryId
        );

        return ApiResponse.success(
                "Category deactivated successfully",
                CategoryResponse.from(category)
        );
    }

    @PatchMapping("/{categoryId}/reactivate")
    public ApiResponse<CategoryResponse> reactivateCategory(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID categoryId
    ) {
        Category category = categoryService.reactivateCategory(
                currentUser.userId(),
                categoryId
        );

        return ApiResponse.success(
                "Category reactivated successfully",
                CategoryResponse.from(category)
        );
    }
}