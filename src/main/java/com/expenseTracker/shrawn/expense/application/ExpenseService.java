package com.expenseTracker.shrawn.expense.application;


import com.expenseTracker.shrawn.category.domain.CategoryRepository;
import com.expenseTracker.shrawn.category.domain.Category;
import com.expenseTracker.shrawn.dashboard.domain.ExpenseDashboard;
import com.expenseTracker.shrawn.dashboard.domain.ExpenseDashboardRepository;
import com.expenseTracker.shrawn.expense.domain.*;
import com.expenseTracker.shrawn.expense.exception.ExpenseNotFoundException;
import com.expenseTracker.shrawn.expense.exception.InvalidExpenseException;
import com.expenseTracker.shrawn.shared.domain.Money;
import com.expenseTracker.shrawn.tag.domain.Tag;
import com.expenseTracker.shrawn.tag.domain.TagRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseDashboardRepository dashboardRepository;
    private final TagRepository tagRepository;
    private final Clock clock;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            CategoryRepository categoryRepository,
            ExpenseDashboardRepository dashboardRepository,
            TagRepository tagRepository,
            Clock clock
    ) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.dashboardRepository = dashboardRepository;
        this.tagRepository = tagRepository;
        this.clock = clock;
    }

    @Transactional
    public Expense createExpense(
            UUID userId,
            Money amount,
            String description,
            ExpenseType type,
            PaymentMethod paymentMethod,
            LocalDate expenseDate,
            UUID dashboardId,
            UUID categoryId,
            Set<UUID> tagIds
    ) {
        ExpenseDashboard dashboard = validateDashboard(userId, dashboardId);

        validateCategory(
                userId,
                categoryId,
                type
        );

        validateTags(
                userId,
                tagIds
        );

        Instant now = Instant.now(clock);

        Expense expense = new Expense(
                null,
                userId,
                dashboard.getId(),
                amount,
                description,
                type,
                paymentMethod,
                expenseDate,
                categoryId,
                tagIds,
                now,
                now
        );

        return expenseRepository.create(
                userId,
                expense
        );
    }

    @Transactional(readOnly = true)
    public Expense getExpense(
            UUID userId,
            UUID expenseId
    ) {
        return expenseRepository.findByIdAndUserId(
                        expenseId,
                        userId
                )
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));
    }

    @Transactional(readOnly = true)
    public Page<Expense> searchExpenses(
            UUID userId,
            ExpenseSearchCriteria criteria,
            Pageable pageable
    ) {
        return expenseRepository.search(
                userId,
                criteria,
                pageable
        );
    }

    @Transactional
    public Expense updateExpense(
            UUID userId,
            UUID expenseId,
            Money amount,
            String description,
            ExpenseType type,
            PaymentMethod paymentMethod,
            LocalDate expenseDate,
            UUID dashboardId,
            UUID categoryId,
            Set<UUID> tagIds
    ) {
        Expense expense = getExpense(
                userId,
                expenseId
        );

        ExpenseDashboard dashboard = validateDashboard(userId, dashboardId);

        validateCategory(
                userId,
                categoryId,
                type
        );

        validateTags(
                userId,
                tagIds
        );

        Instant now = Instant.now(clock);

        expense.update(
                dashboard.getId(),
                amount,
                description,
                type,
                paymentMethod,
                expenseDate,
                categoryId,
                tagIds,
                now
        );

        return expenseRepository.save(expense);
    }

    private ExpenseDashboard validateDashboard(
            UUID userId,
            UUID dashboardId
    ) {
        if (dashboardId == null) {
            return dashboardRepository.findAllActiveByUserId(userId)
                    .stream()
                    .findFirst()
                    .orElseGet(() -> dashboardRepository.create(
                            userId,
                            "Main Dashboard",
                            "Default dashboard for expenses."
                    ));
        }

        ExpenseDashboard dashboard = dashboardRepository.findByIdAndUserId(
                        dashboardId,
                        userId
                )
                .orElseThrow(() -> new InvalidExpenseException(
                        "Selected dashboard does not exist"
                ));

        if (!dashboard.isActive()) {
            throw new InvalidExpenseException(
                    "Selected dashboard is inactive"
            );
        }

        return dashboard;
    }

    @Transactional
    public void deleteExpense(
            UUID userId,
            UUID expenseId
    ) {
        expenseRepository.deleteByIdAndUserId(
                expenseId,
                userId
        );
    }

    private void validateCategory(
            UUID userId,
            UUID categoryId,
            ExpenseType expenseType
    ) {
        Category category = categoryRepository
                .findByIdAndUserId(
                        categoryId,
                        userId
                )
                .orElseThrow(() -> new InvalidExpenseException(
                        "Selected category does not exist"
                ));

        if (!category.isActive()) {
            throw new InvalidExpenseException(
                    "Selected category is inactive"
            );
        }

        if (!category.getType().name().equals(expenseType.name())) {
            throw new InvalidExpenseException(
                    "Expense type must match category type"
            );
        }
    }

    private void validateTags(
            UUID userId,
            Set<UUID> tagIds
    ) {
        Set<UUID> normalizedTagIds = Expense.normalizeTagIds(tagIds);

        for (UUID tagId : normalizedTagIds) {
            Tag tag = tagRepository.findByIdAndUserId(
                            tagId,
                            userId
                    )
                    .orElseThrow(() -> new InvalidExpenseException(
                            "Selected tag does not exist: " + tagId
                    ));

            if (!tag.isActive()) {
                throw new InvalidExpenseException(
                        "Selected tag is inactive: " + tagId
                );
            }
        }
    }
}
