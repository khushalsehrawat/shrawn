package com.expenseTracker.shrawn.user.domain;


import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public record EmailAddress(String value) {

    private static final int MAX_LENGTH = 254;

    private static final Pattern SIMPLE_EMAIL_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
                    Pattern.CASE_INSENSITIVE);

    public EmailAddress {
        Objects.requireNonNull(value, "Email address must not be null");

        value = value.trim().toLowerCase(Locale.ROOT);

        if (value.isBlank()) {
            throw new IllegalArgumentException("Email address must not be blank");
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Email address is too long");
        }

        if (!SIMPLE_EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Email address format is invalid");
        }
    }
}