package com.stavshamir.app.authorization;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokensRepository extends JpaRepository<AuthTokens, String> {
    Optional<AuthTokens> findByUserId(String userId);
}
