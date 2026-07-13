package com.expenseTracker.shrawn.user.exception;



import com.expenseTracker.shrawn.shared.exception.ResourceNotFoundException;

import java.util.UUID;

public final class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException(UUID userId) {
        super("User", userId);
    }
}