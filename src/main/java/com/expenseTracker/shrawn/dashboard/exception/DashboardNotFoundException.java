package com.expenseTracker.shrawn.dashboard.exception;

import com.expenseTracker.shrawn.shared.exception.ResourceNotFoundException;

import java.util.UUID;

public class DashboardNotFoundException extends ResourceNotFoundException {

    public DashboardNotFoundException(UUID dashboardId) {
        super("Expense dashboard", dashboardId);
    }
}
