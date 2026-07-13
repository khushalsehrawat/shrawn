package com.expenseTracker.shrawn.tag.exception;


import com.expenseTracker.shrawn.shared.exception.ResourceNotFoundException;

import java.util.UUID;

public final class TagNotFoundException extends ResourceNotFoundException {

    public TagNotFoundException(UUID tagId) {
        super("Tag", tagId);
    }
}