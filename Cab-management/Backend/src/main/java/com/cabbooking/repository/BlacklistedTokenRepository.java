package com.cabbooking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cabbooking.model.BlacklistedToken;

/**
 * BlacklistedTokenRepository interface for managing blacklisted JWT tokens.
 */
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    /**
     * Finds a blacklisted token by its unique token string.
     *
     * This method is a key part of the security check, allowing the system to
     * determine if a presented JWT is still valid.
     *
     * @param token The JWT string to search for in the blacklist.
     * @return An {@link Optional} containing the {@link BlacklistedToken} if
     * found, otherwise empty.
     */
    Optional<BlacklistedToken> findByToken(String token);
}
