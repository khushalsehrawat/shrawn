package com.expenseTracker.shrawn.shared.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Percentage(BigDecimal value) {

    private static final int SCALE = 2;
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    public Percentage {
        Objects.requireNonNull(value, "Percentage value must not be null");

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Percentage must not be negative");
        }

        value = value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static Percentage zero() {
        return new Percentage(BigDecimal.ZERO);
    }

    public static Percentage calculate(Money part, Money total) {
        Objects.requireNonNull(part, "Part must not be null");
        Objects.requireNonNull(total, "Total must not be null");

        if (total.isZero()) {
            return Percentage.zero();
        }

        BigDecimal result = part.amount()
                .multiply(ONE_HUNDRED)
                .divide(total.amount(), SCALE, RoundingMode.HALF_UP);

        return new Percentage(result);
    }
}