package com.expenseTracker.shrawn.user.domain;


import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findById(UUID userId);

    Optional<User> findByEmail(EmailAddress email);

    Optional<UserAccountCredentials> findCredentialsByEmail(EmailAddress email);

    boolean existsByEmail(EmailAddress email);

    User create(
            EmailAddress email,
            String fullName,
            String passwordHash
    );

    User save(User user);
}