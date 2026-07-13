package com.expenseTracker.shrawn.analytics.api;


import com.expenseTracker.shrawn.analytics.api.dto.*;
import com.expenseTracker.shrawn.analytics.applications.AnalyticsService;
import com.expenseTracker.shrawn.shared.api.ApiResponse;
import com.expenseTracker.shrawn.shared.security.AuthenticatedUser;
import com.expenseTracker.shrawn.shared.security.CurrentUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public ApiResponse<SpendingSummaryResponse> getSummary(
            @CurrentUser AuthenticatedUser currentUser,
            @ModelAttribute AnalyticsQueryRequest request
    ) {
        SpendingSummaryResponse response = SpendingSummaryResponse.from(
                analyticsService.getSummary(
                        currentUser.userId(),
                        request.dashboardId(),
                        request.explicitDateRange(),
                        request.month()
                )
        );

        return ApiResponse.success(
                "Spending summary fetched successfully",
                response
        );
    }

    @GetMapping("/by-category")
    public ApiResponse<List<CategorySpendingResponse>> getSpendingByCategory(
            @CurrentUser AuthenticatedUser currentUser,
            @ModelAttribute AnalyticsQueryRequest request
    ) {
        List<CategorySpendingResponse> response =
                analyticsService.getSpendingByCategory(
                                currentUser.userId(),
                                request.dashboardId(),
                                request.explicitDateRange(),
                                request.month()
                        )
                        .stream()
                        .map(CategorySpendingResponse::from)
                        .toList();

        return ApiResponse.success(
                "Category spending fetched successfully",
                response
        );
    }

    @GetMapping("/by-payment-method")
    public ApiResponse<List<PaymentMethodSpendingResponse>> getSpendingByPaymentMethod(
            @CurrentUser AuthenticatedUser currentUser,
            @ModelAttribute AnalyticsQueryRequest request
    ) {
        List<PaymentMethodSpendingResponse> response =
                analyticsService.getSpendingByPaymentMethod(
                                currentUser.userId(),
                                request.dashboardId(),
                                request.explicitDateRange(),
                                request.month()
                        )
                        .stream()
                        .map(PaymentMethodSpendingResponse::from)
                        .toList();

        return ApiResponse.success(
                "Payment method spending fetched successfully",
                response
        );
    }

    @GetMapping("/by-tag")
    public ApiResponse<List<TagSpendingResponse>> getSpendingByTag(
            @CurrentUser AuthenticatedUser currentUser,
            @ModelAttribute AnalyticsQueryRequest request
    ) {
        List<TagSpendingResponse> response =
                analyticsService.getSpendingByTag(
                                currentUser.userId(),
                                request.dashboardId(),
                                request.explicitDateRange(),
                                request.month()
                        )
                        .stream()
                        .map(TagSpendingResponse::from)
                        .toList();

        return ApiResponse.success(
                "Tag spending fetched successfully",
                response
        );
    }

    @GetMapping("/daily")
    public ApiResponse<List<DailySpendingResponse>> getDailySpending(
            @CurrentUser AuthenticatedUser currentUser,
            @ModelAttribute AnalyticsQueryRequest request
    ) {
        List<DailySpendingResponse> response =
                analyticsService.getDailySpending(
                                currentUser.userId(),
                                request.dashboardId(),
                                request.explicitDateRange(),
                                request.month()
                        )
                        .stream()
                        .map(DailySpendingResponse::from)
                        .toList();

        return ApiResponse.success(
                "Daily spending fetched successfully",
                response
        );
    }
}
