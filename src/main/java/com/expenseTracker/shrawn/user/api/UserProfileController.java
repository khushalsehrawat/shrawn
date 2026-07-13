package com.expenseTracker.shrawn.user.api;


import com.expenseTracker.shrawn.shared.api.ApiResponse;
import com.expenseTracker.shrawn.shared.security.AuthenticatedUser;
import com.expenseTracker.shrawn.shared.security.CurrentUser;
import com.expenseTracker.shrawn.user.api.dto.UpdateUserProfileRequest;
import com.expenseTracker.shrawn.user.api.dto.UserResponse;
import com.expenseTracker.shrawn.user.application.UserProfileService;
import com.expenseTracker.shrawn.user.domain.User;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyProfile(
            @CurrentUser AuthenticatedUser currentUser
    ) {
        User user = userProfileService.getProfile(currentUser.userId());

        return ApiResponse.success(
                "User profile fetched successfully",
                UserResponse.from(user)
        );
    }

    @PutMapping("/me")
    public ApiResponse<UserResponse> updateMyProfile(
            @CurrentUser AuthenticatedUser currentUser,
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        User user = userProfileService.updateProfile(
                currentUser.userId(),
                request.fullName()
        );

        return ApiResponse.success(
                "User profile updated successfully",
                UserResponse.from(user)
        );
    }
}