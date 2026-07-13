/*
package com.expenseTracker.shrawn.user.application;

import com.expenseTracker.shrawn.user.domain.EmailAddress;
import com.expenseTracker.shrawn.user.domain.User;
import com.expenseTracker.shrawn.user.domain.UserRepository;
import com.expenseTracker.shrawn.user.exception.EmailAlreadyExistsException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;

    public UserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User register(
            String email,
            String fullName
    ) {
        EmailAddress emailAddress = new EmailAddress(email);

        if (userRepository.existsByEmail(emailAddress)) {
            throw new EmailAlreadyExistsException();
        }

        return userRepository.create(
                emailAddress,
                fullName
        );
    }
}*/
