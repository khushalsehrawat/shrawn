package com.expenseTracker.shrawn.shared.exception;

public class ConflictException extends ApplicationException {

    public ConflictException(String message) {
        super(ErrorCode.CONFLICT, message);
    }

    public ConflictException(
            String message,
            Throwable cause
    ) {
        super(ErrorCode.CONFLICT, message, cause);
    }
}