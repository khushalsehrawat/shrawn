package com.expenseTracker.shrawn.auth.exception;


import com.expenseTracker.shrawn.shared.security.UnauthorizedException;

public final class TokenExpiredException extends UnauthorizedException {

    public TokenExpiredException(String message) {
        super(message);
    }
}