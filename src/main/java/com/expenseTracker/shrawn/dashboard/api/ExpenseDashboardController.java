package com.expenseTracker.shrawn.dashboard.api;

import com.expenseTracker.shrawn.dashboard.api.dto.CreateExpenseDashboardRequest;
import com.expenseTracker.shrawn.dashboard.api.dto.ExpenseDashboardResponse;
import com.expenseTracker.shrawn.dashboard.api.dto.UpdateExpenseDashboardRequest;
import com.expenseTracker.shrawn.dashboard.application.ExpenseDashboardService;
import com.expenseTracker.shrawn.dashboard.domain.ExpenseDashboard;
import com.expenseTracker.shrawn.shared.api.ApiResponse;
import com.expenseTracker.shrawn.shared.security.AuthenticatedUser;
import com.expenseTracker.shrawn.shared.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expense-dashboards")
public class ExpenseDashboardController {

    private final ExpenseDashboardService dashboardService;

    public ExpenseDashboardController(ExpenseDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ExpenseDashboardResponse> createDashboard(
            @CurrentUser AuthenticatedUser currentUser,
            @Valid @RequestBody CreateExpenseDashboardRequest request
    ) {
        ExpenseDashboard dashboard = dashboardService.createDashboard(
                currentUser.userId(),
                request.name(),
                request.description()
        );

        return ApiResponse.success(
                "Expense dashboard created successfully",
                ExpenseDashboardResponse.from(dashboard)
        );
    }

    @GetMapping
    public ApiResponse<List<ExpenseDashboardResponse>> getDashboards(
            @CurrentUser AuthenticatedUser currentUser
    ) {
        List<ExpenseDashboardResponse> response = dashboardService.getDashboards(currentUser.userId())
                .stream()
                .map(ExpenseDashboardResponse::from)
                .toList();

        return ApiResponse.success(
                "Expense dashboards fetched successfully",
                response
        );
    }

    @GetMapping("/{dashboardId}")
    public ApiResponse<ExpenseDashboardResponse> getDashboard(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID dashboardId
    ) {
        ExpenseDashboard dashboard = dashboardService.getDashboard(currentUser.userId(), dashboardId);
        return ApiResponse.success(
                "Expense dashboard fetched successfully",
                ExpenseDashboardResponse.from(dashboard)
        );
    }

    @PutMapping("/{dashboardId}")
    public ApiResponse<ExpenseDashboardResponse> updateDashboard(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID dashboardId,
            @Valid @RequestBody UpdateExpenseDashboardRequest request
    ) {
        ExpenseDashboard dashboard = dashboardService.updateDashboard(
                currentUser.userId(),
                dashboardId,
                request.name(),
                request.description()
        );

        return ApiResponse.success(
                "Expense dashboard updated successfully",
                ExpenseDashboardResponse.from(dashboard)
        );
    }

    @PatchMapping("/{dashboardId}/deactivate")
    public ApiResponse<ExpenseDashboardResponse> deactivateDashboard(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID dashboardId
    ) {
        ExpenseDashboard dashboard = dashboardService.deactivateDashboard(currentUser.userId(), dashboardId);
        return ApiResponse.success(
                "Expense dashboard deactivated successfully",
                ExpenseDashboardResponse.from(dashboard)
        );
    }
}
