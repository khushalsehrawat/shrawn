package com.expenseTracker.shrawn.auth.domain;


public enum TokenStatus {

    ACTIVE,
    REVOKED,
    EXPIRED;

    public boolean isUsable() {
        return this == ACTIVE;
    }
}