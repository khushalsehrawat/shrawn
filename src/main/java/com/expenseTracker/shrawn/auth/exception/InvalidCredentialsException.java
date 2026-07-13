package com.expenseTracker.shrawn.auth.exception;


import com.expenseTracker.shrawn.shared.security.UnauthorizedException;

public final class InvalidCredentialsException extends UnauthorizedException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}