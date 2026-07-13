package com.expenseTracker.shrawn.shared.web;


public final class WebConstants {

    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String TRACE_ID_MDC_KEY = "traceId";

    public static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";
    public static final String REAL_IP_HEADER = "X-Real-IP";

    private WebConstants() {
        // Utility class.
    }
}