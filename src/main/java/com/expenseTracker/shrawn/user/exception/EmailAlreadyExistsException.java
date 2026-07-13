package com.expenseTracker.shrawn.user.exception;


import com.expenseTracker.shrawn.shared.exception.ConflictException;

public final class EmailAlreadyExistsException extends ConflictException {

    public EmailAlreadyExistsException() {
        super("An account already exists with this email address");
    }
}