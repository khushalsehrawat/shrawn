package com.expenseTracker.shrawn.shared.web;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientIpResolver {

    public String resolve(HttpServletRequest request) {
        String forwardedFor = request.getHeader(WebConstants.FORWARDED_FOR_HEADER);

        if (hasText(forwardedFor)) {
            return firstIp(forwardedFor);
        }

        String realIp = request.getHeader(WebConstants.REAL_IP_HEADER);

        if (hasText(realIp)) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }

    private String firstIp(String forwardedFor) {
        return forwardedFor.split(",")[0].trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
