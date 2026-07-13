package com.expenseTracker.shrawn.analytics.applications;



import com.expenseTracker.shrawn.analytics.domain.*;
import com.expenseTracker.shrawn.shared.domain.DateRange;
//import jakarta.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional;


import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final Clock clock;

    public AnalyticsService(
            AnalyticsRepository analyticsRepository,
            Clock clock
    ) {
        this.analyticsRepository = analyticsRepository;
        this.clock = clock;
    }

    public SpendingSummary getSummary(
            UUID userId,
            UUID dashboardId,
            Optional<DateRange> explicitDateRange,
            YearMonth month
    ) {
        DateRange dateRange = resolveDateRange(
                explicitDateRange,
                month
        );

        return analyticsRepository.getSummary(
                userId,
                dashboardId,
                dateRange
        );
    }

    public List<CategorySpending> getSpendingByCategory(
            UUID userId,
            UUID dashboardId,
            Optional<DateRange> explicitDateRange,
            YearMonth month
    ) {
        DateRange dateRange = resolveDateRange(
                explicitDateRange,
                month
        );

        return analyticsRepository.getSpendingByCategory(
                userId,
                dashboardId,
                dateRange
        );
    }

    public List<PaymentMethodSpending> getSpendingByPaymentMethod(
            UUID userId,
            UUID dashboardId,
            Optional<DateRange> explicitDateRange,
            YearMonth month
    ) {
        DateRange dateRange = resolveDateRange(
                explicitDateRange,
                month
        );

        return analyticsRepository.getSpendingByPaymentMethod(
                userId,
                dashboardId,
                dateRange
        );
    }

    public List<TagSpending> getSpendingByTag(
            UUID userId,
            UUID dashboardId,
            Optional<DateRange> explicitDateRange,
            YearMonth month
    ) {
        DateRange dateRange = resolveDateRange(
                explicitDateRange,
                month
        );

        return analyticsRepository.getSpendingByTag(
                userId,
                dashboardId,
                dateRange
        );
    }

    public List<DailySpending> getDailySpending(
            UUID userId,
            UUID dashboardId,
            Optional<DateRange> explicitDateRange,
            YearMonth month
    ) {
        DateRange dateRange = resolveDateRange(
                explicitDateRange,
                month
        );

        return analyticsRepository.getDailySpending(
                userId,
                dashboardId,
                dateRange
        );
    }

    private DateRange resolveDateRange(
            Optional<DateRange> explicitDateRange,
            YearMonth month
    ) {
        Optional<DateRange> normalizedExplicitRange =
                explicitDateRange == null
                        ? Optional.empty()
                        : explicitDateRange;

        if (normalizedExplicitRange.isPresent() && month != null) {
            throw new ValidationException(
                    "Use either startDate/endDate or month, not both"
            );
        }

        if (normalizedExplicitRange.isPresent()) {
            return normalizedExplicitRange.get();
        }

        if (month != null) {
            return new DateRange(
                    month.atDay(1),
                    month.atEndOfMonth()
            );
        }

        YearMonth currentMonth = YearMonth.from(
                LocalDate.now(clock)
        );

        return new DateRange(
                currentMonth.atDay(1),
                currentMonth.atEndOfMonth()
        );
    }
}
