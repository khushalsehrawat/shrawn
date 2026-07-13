package com.expenseTracker.shrawn.category.application;


import com.expenseTracker.shrawn.category.domain.CategoryType;
import com.expenseTracker.shrawn.category.infrastructure.persistence.CategoryJpaEntity;
import com.expenseTracker.shrawn.category.infrastructure.persistence.SpringDataCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DefaultCategoryService {

    private final SpringDataCategoryRepository categoryRepository;

    public DefaultCategoryService(
            SpringDataCategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public void createDefaultCategoriesForUser(UUID userId) {
        createExpenseCategories(userId);
        createIncomeCategories(userId);
    }

    private void createExpenseCategories(UUID userId) {
        List<DefaultCategory> expenseCategories = List.of(
                new DefaultCategory("Food", "Meals, snacks and restaurants"),
                new DefaultCategory("Travel", "Taxi, fuel, bus, train and trips"),
                new DefaultCategory("Rent", "House rent and accommodation"),
                new DefaultCategory("Shopping", "Clothes, gadgets and personal shopping"),
                new DefaultCategory("Bills", "Electricity, internet, mobile and utilities"),
                new DefaultCategory("Medical", "Medicines, doctor visits and healthcare"),
                new DefaultCategory("Education", "Books, courses and learning"),
                new DefaultCategory("Entertainment", "Movies, games and subscriptions"),
                new DefaultCategory("Groceries", "Daily household food and supplies"),
                new DefaultCategory("Other Expense", "Other expense transactions")
        );

        expenseCategories.forEach(category ->
                createCategoryIfMissing(
                        userId,
                        category.name(),
                        category.description(),
                        CategoryType.EXPENSE
                )
        );
    }

    private void createIncomeCategories(UUID userId) {
        List<DefaultCategory> incomeCategories = List.of(
                new DefaultCategory("Salary", "Monthly salary and job income"),
                new DefaultCategory("Freelance", "Freelance and contract income"),
                new DefaultCategory("Business", "Business income"),
                new DefaultCategory("Refund", "Returned money and refunds"),
                new DefaultCategory("Cashback", "Cashback and rewards"),
                new DefaultCategory("Other Income", "Other income transactions")
        );

        incomeCategories.forEach(category ->
                createCategoryIfMissing(
                        userId,
                        category.name(),
                        category.description(),
                        CategoryType.INCOME
                )
        );
    }

    private void createCategoryIfMissing(
            UUID userId,
            String name,
            String description,
            CategoryType type
    ) {
        boolean alreadyExists = categoryRepository.existsByUserIdAndNameIgnoreCaseAndType(
                userId,
                name,
                type
        );

        if (alreadyExists) {
            return;
        }

        CategoryJpaEntity category = new CategoryJpaEntity(
                userId,
                name,
                description,
                type,
                true
        );

        categoryRepository.save(category);
    }

    private record DefaultCategory(
            String name,
            String description
    ) {
    }
}