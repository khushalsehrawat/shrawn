package com.expenseTracker.shrawn.user.infrastructure.persistence;


import com.expenseTracker.shrawn.user.domain.EmailAddress;
import com.expenseTracker.shrawn.user.domain.User;
import com.expenseTracker.shrawn.user.domain.UserAccountCredentials;
import com.expenseTracker.shrawn.user.domain.UserRepository;
import com.expenseTracker.shrawn.user.exception.UserNotFoundException;
import com.expenseTracker.shrawn.user.infrastructure.UserJpaEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;
    private final UserPersistenceMapper mapper;

    public JpaUserRepositoryAdapter(
            SpringDataUserRepository springDataUserRepository,
            UserPersistenceMapper mapper
    ) {
        this.springDataUserRepository = springDataUserRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID userId) {
        return springDataUserRepository.findById(userId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(EmailAddress email) {
        return springDataUserRepository.findByEmail(email.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAccountCredentials> findCredentialsByEmail(
            EmailAddress email
    ) {
        return springDataUserRepository.findByEmail(email.value())
                .map(mapper::toCredentials);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(EmailAddress email) {
        return springDataUserRepository.existsByEmail(email.value());
    }

    @Override
    public User create(
            EmailAddress email,
            String fullName,
            String passwordHash
    ) {
        UserJpaEntity entity = mapper.toNewEntity(
                email,
                fullName,
                passwordHash
        );

        UserJpaEntity savedEntity = springDataUserRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = springDataUserRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException(user.getId()));

        mapper.updateEntityFromDomain(user, entity);

        UserJpaEntity savedEntity = springDataUserRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }


}