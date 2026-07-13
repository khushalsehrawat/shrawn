package com.expenseTracker.shrawn.tag.api.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTagRequest(

        @NotBlank(message = "Tag name must not be blank")
        @Size(max = 40, message = "Tag name must not exceed 40 characters")
        String name
) {
}