package com.expenseTracker.shrawn.auth.exception;


import com.expenseTracker.shrawn.shared.security.UnauthorizedException;

public final class InvalidTokenException extends UnauthorizedException {

    public InvalidTokenException(String message) {
        super(message);
    }
}