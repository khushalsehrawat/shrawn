package com.expenseTracker.shrawn.budget.exception;

import com.expenseTracker.shrawn.shared.exception.ResourceNotFoundException;

import java.util.UUID;

public final class BudgetNotFoundException extends ResourceNotFoundException {

    public BudgetNotFoundException(UUID budgetId) {
        super("Budget", budgetId);
    }
}
