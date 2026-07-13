package com.expenseTracker.shrawn.auth.api.dto;


import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(

        @NotBlank(message = "Refresh token must not be blank")
        String refreshToken
) {
}