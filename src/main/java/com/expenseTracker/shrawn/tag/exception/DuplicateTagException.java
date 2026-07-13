package com.expenseTracker.shrawn.tag.exception;


import com.expenseTracker.shrawn.shared.exception.ConflictException;

public final class DuplicateTagException extends ConflictException {

    public DuplicateTagException() {
        super("A tag with this name already exists");
    }
}