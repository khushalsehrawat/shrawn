package com.expenseTracker.shrawn.analytics.infrastructure.persistence;


import com.expenseTracker.shrawn.analytics.domain.*;
import com.expenseTracker.shrawn.expense.domain.PaymentMethod;
import com.expenseTracker.shrawn.shared.domain.DateRange;
import com.expenseTracker.shrawn.shared.domain.Money;
import com.expenseTracker.shrawn.shared.domain.Percentage;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class JpaAnalyticsRepositoryAdapter implements AnalyticsRepository {

    private final SpringDataAnalyticsRepository springDataAnalyticsRepository;

    public JpaAnalyticsRepositoryAdapter(
            SpringDataAnalyticsRepository springDataAnalyticsRepository
    ) {
        this.springDataAnalyticsRepository = springDataAnalyticsRepository;
    }

    @Override
    public SpendingSummary getSummary(
            UUID userId,
            UUID dashboardId,
            DateRange dateRange
    ) {
        Object[] raw = springDataAnalyticsRepository.getSummaryRaw(
                userId,
                dashboardId,
                dateRange.startDate(),
                dateRange.endDate()
        );

        BigDecimal totalExpense = toBigDecimal(raw[0]);
        BigDecimal totalIncome = toBigDecimal(raw[1]);
        long transactionCount = toLong(raw[2]);

        Money expenseMoney = new Money(totalExpense);
        Money incomeMoney = new Money(totalIncome);
        Money totalFlow = expenseMoney.add(incomeMoney);

        Money netAmount = incomeMoney.isGreaterThanOrEqualTo(expenseMoney)
                ? incomeMoney.subtract(expenseMoney)
                : Money.zero();

        return new SpendingSummary(
                expenseMoney,
                incomeMoney,
                netAmount,
                transactionCount,
                Percentage.calculate(
                        expenseMoney,
                        totalFlow
                )
        );
    }

    @Override
    public List<CategorySpending> getSpendingByCategory(
            UUID userId,
            UUID dashboardId,
            DateRange dateRange
    ) {
        List<Object[]> rawRows =
                springDataAnalyticsRepository.getSpendingByCategoryRaw(
                        userId,
                        dashboardId,
                        dateRange.startDate(),
                        dateRange.endDate()
                );

        Money total = totalAmount(rawRows, 2);

        return rawRows.stream()
                .map(row -> {
                    Money amount = new Money(toBigDecimal(row[2]));

                    return new CategorySpending(
                            (UUID) row[0],
                            (String) row[1],
                            amount,
                            toLong(row[3]),
                            Percentage.calculate(amount, total)
                    );
                })
                .toList();
    }

    @Override
    public List<PaymentMethodSpending> getSpendingByPaymentMethod(
            UUID userId,
            UUID dashboardId,
            DateRange dateRange
    ) {
        List<Object[]> rawRows =
                springDataAnalyticsRepository.getSpendingByPaymentMethodRaw(
                        userId,
                        dashboardId,
                        dateRange.startDate(),
                        dateRange.endDate()
                );

        Money total = totalAmount(rawRows, 1);

        return rawRows.stream()
                .map(row -> {
                    Money amount = new Money(toBigDecimal(row[1]));

                    return new PaymentMethodSpending(
                            (PaymentMethod) row[0],
                            amount,
                            toLong(row[2]),
                            Percentage.calculate(amount, total)
                    );
                })
                .toList();
    }

    @Override
    public List<TagSpending> getSpendingByTag(
            UUID userId,
            UUID dashboardId,
            DateRange dateRange
    ) {
        List<Object[]> rawRows =
                springDataAnalyticsRepository.getSpendingByTagRaw(
                        userId,
                        dashboardId,
                        dateRange.startDate(),
                        dateRange.endDate()
                );

        Money total = totalAmount(rawRows, 2);

        return rawRows.stream()
                .map(row -> {
                    Money amount = new Money(toBigDecimal(row[2]));

                    return new TagSpending(
                            (UUID) row[0],
                            (String) row[1],
                            amount,
                            toLong(row[3]),
                            Percentage.calculate(amount, total)
                    );
                })
                .toList();
    }

    @Override
    public List<DailySpending> getDailySpending(
            UUID userId,
            UUID dashboardId,
            DateRange dateRange
    ) {
        return springDataAnalyticsRepository.getDailySpendingRaw(
                        userId,
                        dashboardId,
                        dateRange.startDate(),
                        dateRange.endDate()
                )
                .stream()
                .map(row -> new DailySpending(
                        (LocalDate) row[0],
                        new Money(toBigDecimal(row[1])),
                        toLong(row[2])
                ))
                .toList();
    }


    private Money totalAmount(
            List<Object[]> rows,
            int amountIndex
    ) {
        BigDecimal total = rows.stream()
                .map(row -> toBigDecimal(row[amountIndex]))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Money(total);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }

        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }

        throw new IllegalArgumentException("Value cannot be converted to BigDecimal");
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0L;
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        throw new IllegalArgumentException("Value cannot be converted to long");
    }
}
