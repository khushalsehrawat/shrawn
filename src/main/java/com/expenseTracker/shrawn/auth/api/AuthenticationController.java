package com.expenseTracker.shrawn.auth.api;


import com.expenseTracker.shrawn.auth.api.dto.*;
import com.expenseTracker.shrawn.auth.application.AuthenticationService;
import com.expenseTracker.shrawn.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(
            AuthenticationService authenticationService
    ) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse response = authenticationService.register(
                request.email(),
                request.fullName(),
                request.password()
        );

        return ApiResponse.success(
                "Registration successful",
                response
        );
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = authenticationService.login(
                request.email(),
                request.password()
        );

        return ApiResponse.success(
                "Login successful",
                response
        );
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        AuthResponse response = authenticationService.refresh(
                request.refreshToken()
        );

        return ApiResponse.success(
                "Token refreshed successfully",
                response
        );
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        authenticationService.logout(request.refreshToken());

        return ApiResponse.success("Logout successful");
    }
}