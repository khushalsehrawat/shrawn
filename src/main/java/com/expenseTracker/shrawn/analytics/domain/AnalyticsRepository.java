package com.expenseTracker.shrawn.analytics.domain;


import com.expenseTracker.shrawn.shared.domain.DateRange;

import java.util.List;
import java.util.UUID;

public interface AnalyticsRepository {

    SpendingSummary getSummary(
            UUID userId,
            UUID dashboardId,
            DateRange dateRange
    );

    List<CategorySpending> getSpendingByCategory(
            UUID userId,
            UUID dashboardId,
            DateRange dateRange
    );

    List<PaymentMethodSpending> getSpendingByPaymentMethod(
            UUID userId,
            UUID dashboardId,
            DateRange dateRange
    );

    List<TagSpending> getSpendingByTag(
            UUID userId,
            UUID dashboardId,
            DateRange dateRange
    );

    List<DailySpending> getDailySpending(
            UUID userId,
            UUID dashboardId,
            DateRange dateRange
    );
}
