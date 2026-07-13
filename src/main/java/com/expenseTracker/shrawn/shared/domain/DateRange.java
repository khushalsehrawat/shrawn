package com.expenseTracker.shrawn.shared.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public record DateRange(LocalDate startDate, LocalDate endDate) {

    public DateRange {
        Objects.requireNonNull(startDate, "Start date must not be null");
        Objects.requireNonNull(endDate, "End date must not be null");

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must not be before start date");
        }
    }

    public static DateRange singleDay(LocalDate date) {
        return new DateRange(date, date);
    }

    public boolean contains(LocalDate date) {
        Objects.requireNonNull(date, "Date must not be null");

        return !date.isBefore(startDate)
                && !date.isAfter(endDate);
    }

    public long daysInclusive() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
}