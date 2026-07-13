package com.expenseTracker.shrawn.dashboard.exception;

import com.expenseTracker.shrawn.shared.exception.ConflictException;

public class DuplicateDashboardException extends ConflictException {

    public DuplicateDashboardException(String name) {
        super("Dashboard already exists with name: " + name);
    }
}
