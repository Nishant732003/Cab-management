package com.cabbooking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cabbooking.model.BlacklistedToken;

/**
 * Spring Data JPA repository for {@link BlacklistedToken} entities.
 *
 * Main Responsibilities:
 * - Provides standard CRUD (Create, Read, Update, Delete) operations for the token blacklist.
 * - Allows the security filter to efficiently check if a JWT has been blacklisted.
 *
 * Workflow:
 * - When a user logs out, the `LogoutService` saves their JWT to this repository.
 * - The `JwtAuthenticationFilter` queries this repository for every incoming request
 * to ensure the token has not been invalidated.
 */
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    /**
     * Finds a blacklisted token by its unique token string.
     *
     * This method is a key part of the security check, allowing the system
     * to determine if a presented JWT is still valid.
     *
     * @param token The JWT string to search for in the blacklist.
     * @return An {@link Optional} containing the {@link BlacklistedToken} if found, otherwise empty.
     */
    Optional<BlacklistedToken> findByToken(String token);
}