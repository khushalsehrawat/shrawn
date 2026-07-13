package com.expenseTracker.shrawn.budget.api;


import com.expenseTracker.shrawn.budget.api.dto.BudgetResponse;
import com.expenseTracker.shrawn.budget.api.dto.CreateBudgetRequest;
import com.expenseTracker.shrawn.budget.api.dto.UpdateBudgetRequest;
import com.expenseTracker.shrawn.budget.application.BudgetService;
import com.expenseTracker.shrawn.budget.domain.Budget;
import com.expenseTracker.shrawn.shared.api.ApiResponse;
import com.expenseTracker.shrawn.shared.domain.Money;
import com.expenseTracker.shrawn.shared.security.AuthenticatedUser;
import com.expenseTracker.shrawn.shared.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BudgetResponse> createBudget(
            @CurrentUser AuthenticatedUser currentUser,
            @Valid @RequestBody CreateBudgetRequest request
    ) {
        Budget budget = budgetService.createBudget(
                currentUser.userId(),
                request.name(),
                new Money(request.limitAmount()),
                request.periodType(),
                request.month(),
                request.startDate(),
                request.endDate(),
                request.categoryId()
        );

        return ApiResponse.success(
                "Budget created successfully",
                BudgetResponse.from(budget)
        );
    }

    @GetMapping
    public ApiResponse<List<BudgetResponse>> getBudgets(
            @CurrentUser AuthenticatedUser currentUser,
            @RequestParam(defaultValue = "false") boolean activeOnly
    ) {
        List<BudgetResponse> response = budgetService
                .getBudgets(
                        currentUser.userId(),
                        activeOnly
                )
                .stream()
                .map(BudgetResponse::from)
                .toList();

        return ApiResponse.success(
                "Budgets fetched successfully",
                response
        );
    }

    @GetMapping("/by-date")
    public ApiResponse<List<BudgetResponse>> getBudgetsForDate(
            @CurrentUser AuthenticatedUser currentUser,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        List<BudgetResponse> response = budgetService
                .getBudgetsForDate(
                        currentUser.userId(),
                        date
                )
                .stream()
                .map(BudgetResponse::from)
                .toList();

        return ApiResponse.success(
                "Budgets fetched successfully",
                response
        );
    }

    @GetMapping("/{budgetId}")
    public ApiResponse<BudgetResponse> getBudget(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID budgetId
    ) {
        Budget budget = budgetService.getBudget(
                currentUser.userId(),
                budgetId
        );

        return ApiResponse.success(
                "Budget fetched successfully",
                BudgetResponse.from(budget)
        );
    }

    @PutMapping("/{budgetId}")
    public ApiResponse<BudgetResponse> updateBudget(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID budgetId,
            @Valid @RequestBody UpdateBudgetRequest request
    ) {
        Budget budget = budgetService.updateBudget(
                currentUser.userId(),
                budgetId,
                request.name(),
                new Money(request.limitAmount()),
                request.periodType(),
                request.month(),
                request.startDate(),
                request.endDate(),
                request.categoryId()
        );

        return ApiResponse.success(
                "Budget updated successfully",
                BudgetResponse.from(budget)
        );
    }

    @PatchMapping("/{budgetId}/deactivate")
    public ApiResponse<BudgetResponse> deactivateBudget(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID budgetId
    ) {
        Budget budget = budgetService.deactivateBudget(
                currentUser.userId(),
                budgetId
        );

        return ApiResponse.success(
                "Budget deactivated successfully",
                BudgetResponse.from(budget)
        );
    }

    @PatchMapping("/{budgetId}/reactivate")
    public ApiResponse<BudgetResponse> reactivateBudget(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID budgetId
    ) {
        Budget budget = budgetService.reactivateBudget(
                currentUser.userId(),
                budgetId
        );

        return ApiResponse.success(
                "Budget reactivated successfully",
                BudgetResponse.from(budget)
        );
    }
}