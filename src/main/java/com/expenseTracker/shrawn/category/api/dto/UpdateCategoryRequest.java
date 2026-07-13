package com.expenseTracker.shrawn.category.api.dto;


import com.expenseTracker.shrawn.category.domain.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(

        @NotBlank(message = "Category name must not be blank")
        @Size(max = 60, message = "Category name must not exceed 60 characters")
        String name,

        @Size(max = 255, message = "Category description must not exceed 255 characters")
        String description,

        @NotNull(message = "Category type must not be null")
        CategoryType type
) {
}