package com.expenseTracker.shrawn.dashboard.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateExpenseDashboardRequest(
        @NotBlank(message = "Dashboard name must not be blank")
        @Size(max = 80, message = "Dashboard name must not exceed 80 characters")
        String name,

        @Size(max = 255, message = "Dashboard description must not exceed 255 characters")
        String description
) {
}
