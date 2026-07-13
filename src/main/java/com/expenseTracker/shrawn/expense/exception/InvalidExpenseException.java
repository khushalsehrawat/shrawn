package com.expenseTracker.shrawn.expense.exception;


import jakarta.validation.ValidationException;

public final class InvalidExpenseException extends ValidationException {

    public InvalidExpenseException(String message) {
        super(message);
    }
}