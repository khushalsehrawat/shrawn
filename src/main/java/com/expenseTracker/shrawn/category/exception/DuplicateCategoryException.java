package com.expenseTracker.shrawn.category.exception;

import com.expenseTracker.shrawn.shared.exception.ConflictException;

public final class DuplicateCategoryException extends ConflictException {

    public DuplicateCategoryException() {
        super("A category with this name and type already exists");
    }
}
