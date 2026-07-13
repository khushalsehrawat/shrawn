package com.expenseTracker.shrawn.analytics.api.dto;



import com.expenseTracker.shrawn.shared.domain.DateRange;
import jakarta.validation.ValidationException;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

public record AnalyticsQueryRequest(
        UUID dashboardId,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,

        YearMonth month
) {

    public Optional<DateRange> explicitDateRange() {
        if (startDate == null && endDate == null) {
            return Optional.empty();
        }

        if (startDate == null || endDate == null) {
            throw new ValidationException(
                    "Both startDate and endDate must be provided together"
            );
        }

        return Optional.of(new DateRange(
                startDate,
                endDate
        ));
    }
}
