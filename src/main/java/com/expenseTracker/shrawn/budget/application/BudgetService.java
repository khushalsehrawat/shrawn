package com.expenseTracker.shrawn.budget.application;



import com.expenseTracker.shrawn.budget.domain.Budget;
import com.expenseTracker.shrawn.budget.domain.BudgetPeriodType;
import com.expenseTracker.shrawn.budget.domain.BudgetRepository;
import com.expenseTracker.shrawn.budget.exception.BudgetNotFoundException;
import com.expenseTracker.shrawn.budget.exception.DuplicateBudgetException;
import com.expenseTracker.shrawn.category.domain.Category;
import com.expenseTracker.shrawn.category.domain.CategoryRepository;
import com.expenseTracker.shrawn.category.domain.CategoryType;
import com.expenseTracker.shrawn.shared.domain.DateRange;
import com.expenseTracker.shrawn.shared.domain.Money;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;


import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final Clock clock;

    public BudgetService(
            BudgetRepository budgetRepository,
            CategoryRepository categoryRepository,
            Clock clock
    ) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.clock = clock;
    }

    @Transactional
    public Budget createBudget(
            UUID userId,
            String name,
            Money limitAmount,
            BudgetPeriodType periodType,
            YearMonth month,
            LocalDate startDate,
            LocalDate endDate,
            UUID categoryId
    ) {
        DateRange period = buildPeriod(
                periodType,
                month,
                startDate,
                endDate
        );

        validateCategoryIfPresent(
                userId,
                categoryId
        );

        if (budgetRepository.existsOverlappingBudget(
                userId,
                categoryId,
                period.startDate(),
                period.endDate()
        )) {
            throw new DuplicateBudgetException();
        }

        Instant now = Instant.now(clock);

        Budget budget = new Budget(
                null,
                userId,
                name,
                limitAmount,
                periodType,
                period,
                categoryId,
                true,
                now,
                now
        );

        return budgetRepository.create(budget);
    }

    @Transactional(readOnly = true)
    public List<Budget> getBudgets(
            UUID userId,
            boolean activeOnly
    ) {
        return activeOnly
                ? budgetRepository.findAllActiveByUserId(userId)
                : budgetRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Budget> getBudgetsForDate(
            UUID userId,
            LocalDate date
    ) {
        Objects.requireNonNull(date, "Date must not be null");

        return budgetRepository.findActiveBudgetsForDate(
                userId,
                date
        );
    }

    @Transactional(readOnly = true)
    public Budget getBudget(
            UUID userId,
            UUID budgetId
    ) {
        return budgetRepository.findByIdAndUserId(
                        budgetId,
                        userId
                )
                .orElseThrow(() -> new BudgetNotFoundException(budgetId));
    }

    @Transactional
    public Budget updateBudget(
            UUID userId,
            UUID budgetId,
            String name,
            Money limitAmount,
            BudgetPeriodType periodType,
            YearMonth month,
            LocalDate startDate,
            LocalDate endDate,
            UUID categoryId
    ) {
        Budget budget = getBudget(
                userId,
                budgetId
        );

        DateRange period = buildPeriod(
                periodType,
                month,
                startDate,
                endDate
        );

        validateCategoryIfPresent(
                userId,
                categoryId
        );

        if (budgetRepository.existsOverlappingBudgetExcludingId(
                userId,
                categoryId,
                period.startDate(),
                period.endDate(),
                budgetId
        )) {
            throw new DuplicateBudgetException();
        }

        Instant now = Instant.now(clock);

        budget.update(
                name,
                limitAmount,
                periodType,
                period,
                categoryId,
                now
        );

        return budgetRepository.save(budget);
    }

    @Transactional
    public Budget deactivateBudget(
            UUID userId,
            UUID budgetId
    ) {
        Budget budget = getBudget(
                userId,
                budgetId
        );

        Instant now = Instant.now(clock);

        budget.deactivate(now);

        return budgetRepository.save(budget);
    }

    @Transactional
    public Budget reactivateBudget(
            UUID userId,
            UUID budgetId
    ) {
        Budget budget = getBudget(
                userId,
                budgetId
        );

        if (budgetRepository.existsOverlappingBudgetExcludingId(
                userId,
                budget.getCategoryId(),
                budget.getPeriod().startDate(),
                budget.getPeriod().endDate(),
                budgetId
        )) {
            throw new DuplicateBudgetException();
        }

        Instant now = Instant.now(clock);

        budget.reactivate(now);

        return budgetRepository.save(budget);
    }

    private DateRange buildPeriod(
            BudgetPeriodType periodType,
            YearMonth month,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Objects.requireNonNull(periodType, "Budget period type must not be null");

        return switch (periodType) {
            case MONTHLY -> {
                if (month == null) {
                    throw new ValidationException(
                            "Month is required for monthly budget"
                    );
                }

                yield Budget.monthlyPeriod(month);
            }

            case CUSTOM -> {
                if (startDate == null || endDate == null) {
                    throw new ValidationException(
                            "Start date and end date are required for custom budget"
                    );
                }

                yield new DateRange(
                        startDate,
                        endDate
                );
            }
        };
    }

    private void validateCategoryIfPresent(
            UUID userId,
            UUID categoryId
    ) {
        if (categoryId == null) {
            return;
        }

        Category category = categoryRepository
                .findByIdAndUserId(
                        categoryId,
                        userId
                )
                .orElseThrow(() -> new ValidationException(
                        "Selected budget category does not exist"
                ));

        if (!category.isActive()) {
            throw new ValidationException(
                    "Selected budget category is inactive"
            );
        }

        if (category.getType() != CategoryType.EXPENSE) {
            throw new ValidationException(
                    "Budget category must be an expense category"
            );
        }
    }
}