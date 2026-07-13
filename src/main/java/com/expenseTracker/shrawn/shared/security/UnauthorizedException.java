package com.expenseTracker.shrawn.shared.security;


import com.expenseTracker.shrawn.shared.exception.ApplicationException;
import com.expenseTracker.shrawn.shared.exception.ErrorCode;

public class UnauthorizedException extends ApplicationException {

    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}