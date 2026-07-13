package com.expenseTracker.shrawn.budget.exception;


import com.expenseTracker.shrawn.shared.exception.ConflictException;

public final class DuplicateBudgetException extends ConflictException {

    public DuplicateBudgetException() {
        super("A budget already exists for this period");
    }
}