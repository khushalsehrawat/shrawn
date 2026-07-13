package com.expenseTracker.shrawn.expense.api;


import com.expenseTracker.shrawn.expense.api.dto.CreateExpenseRequest;
import com.expenseTracker.shrawn.expense.api.dto.ExpenseResponse;
import com.expenseTracker.shrawn.expense.api.dto.ExpenseSearchRequest;
import com.expenseTracker.shrawn.expense.api.dto.UpdateExpenseRequest;
import com.expenseTracker.shrawn.expense.application.ExpenseService;
import com.expenseTracker.shrawn.expense.domain.Expense;
import com.expenseTracker.shrawn.shared.api.ApiResponse;
import com.expenseTracker.shrawn.shared.api.PageResponse;
import com.expenseTracker.shrawn.shared.domain.Money;
import com.expenseTracker.shrawn.shared.security.AuthenticatedUser;
import com.expenseTracker.shrawn.shared.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ExpenseResponse> createExpense(
            @CurrentUser AuthenticatedUser currentUser,
            @Valid @RequestBody CreateExpenseRequest request
    ) {
        Expense expense = expenseService.createExpense(
                currentUser.userId(),
                new Money(request.amount()),
                request.description(),
                request.type(),
                request.paymentMethod(),
                request.expenseDate(),
                request.dashboardId(),
                request.categoryId(),
                request.tagIds()
        );

        return ApiResponse.success(
                "Expense created successfully",
                ExpenseResponse.from(expense)
        );
    }

    @GetMapping("/{expenseId}")
    public ApiResponse<ExpenseResponse> getExpense(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID expenseId
    ) {
        Expense expense = expenseService.getExpense(
                currentUser.userId(),
                expenseId
        );

        return ApiResponse.success(
                "Expense fetched successfully",
                ExpenseResponse.from(expense)
        );
    }

    @GetMapping
    public ApiResponse<PageResponse<ExpenseResponse>> searchExpenses(
            @CurrentUser AuthenticatedUser currentUser,
            @ModelAttribute ExpenseSearchRequest request,
            Pageable pageable
    ) {
        Page<Expense> expenses = expenseService.searchExpenses(
                currentUser.userId(),
                request.toCriteria(),
                pageable
        );

        return ApiResponse.success(
                "Expenses fetched successfully",
                PageResponse.from(
                        expenses,
                        ExpenseResponse::from
                )
        );
    }

    @PutMapping("/{expenseId}")
    public ApiResponse<ExpenseResponse> updateExpense(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID expenseId,
            @Valid @RequestBody UpdateExpenseRequest request
    ) {
        Expense expense = expenseService.updateExpense(
                currentUser.userId(),
                expenseId,
                new Money(request.amount()),
                request.description(),
                request.type(),
                request.paymentMethod(),
                request.expenseDate(),
                request.dashboardId(),
                request.categoryId(),
                request.tagIds()
        );

        return ApiResponse.success(
                "Expense updated successfully",
                ExpenseResponse.from(expense)
        );
    }

    @DeleteMapping("/{expenseId}")
    public ApiResponse<Void> deleteExpense(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable UUID expenseId
    ) {
        expenseService.deleteExpense(
                currentUser.userId(),
                expenseId
        );

        return ApiResponse.success("Expense deleted successfully");
    }
}
