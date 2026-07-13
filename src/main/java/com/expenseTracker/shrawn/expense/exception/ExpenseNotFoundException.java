package com.expenseTracker.shrawn.expense.exception;


import com.expenseTracker.shrawn.shared.exception.ResourceNotFoundException;

import java.util.UUID;

public final class ExpenseNotFoundException extends ResourceNotFoundException {

    public ExpenseNotFoundException(UUID expenseId) {
        super("Expense", expenseId);
    }
}