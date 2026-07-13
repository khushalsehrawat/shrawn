package com.expenseTracker.shrawn.user.domain;


public enum UserStatus {

    ACTIVE,
    EMAIL_VERIFICATION_PENDING,
    SUSPENDED,
    DELETED;

    public boolean canLogin() {
        return this == ACTIVE;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }
}