package com.expenseTracker.shrawn.shared.web;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HexFormat;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final int TRACE_ID_BYTES = 16;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String traceId = resolveTraceId(request);

        try {
            MDC.put(WebConstants.TRACE_ID_MDC_KEY, traceId);
            response.setHeader(WebConstants.TRACE_ID_HEADER, traceId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(WebConstants.TRACE_ID_MDC_KEY);
        }
    }

    private String resolveTraceId(HttpServletRequest request) {
        String incomingTraceId = request.getHeader(WebConstants.TRACE_ID_HEADER);

        if (incomingTraceId != null && isValidTraceId(incomingTraceId)) {
            return incomingTraceId.trim();
        }

        return generateTraceId();
    }

    private boolean isValidTraceId(String traceId) {
        String trimmedTraceId = traceId.trim();

        return !trimmedTraceId.isBlank()
                && trimmedTraceId.length() <= 64
                && trimmedTraceId.matches("[a-zA-Z0-9\\-_.]+");
    }

    private String generateTraceId() {
        byte[] bytes = new byte[TRACE_ID_BYTES];
        secureRandom.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}