package com.expenseTracker.shrawn.user.application;


import com.expenseTracker.shrawn.user.domain.User;
import com.expenseTracker.shrawn.user.domain.UserRepository;
import com.expenseTracker.shrawn.user.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final Clock clock;

    public UserProfileService(
            UserRepository userRepository,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public User getProfile(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Transactional
    public User updateProfile(
            UUID userId,
            String fullName
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Instant now = Instant.now(clock);

        user.updateProfile(fullName, now);

        return userRepository.save(user);
    }
}
