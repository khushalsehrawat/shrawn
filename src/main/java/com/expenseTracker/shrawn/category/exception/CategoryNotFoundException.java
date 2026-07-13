package com.expenseTracker.shrawn.category.exception;



import com.expenseTracker.shrawn.shared.exception.ResourceNotFoundException;

import java.util.UUID;

public final class CategoryNotFoundException extends ResourceNotFoundException {

    public CategoryNotFoundException(UUID categoryId) {
        super("Category", categoryId);
    }
}