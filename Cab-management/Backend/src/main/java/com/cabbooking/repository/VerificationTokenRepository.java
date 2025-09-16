package com.cabbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cabbooking.model.VerificationToken;

/*
 * VerificationTokenRepository interface for managing VerificationToken entities.
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    /*
     * Finds a VerificationToken entity by its token value.
     *
     * @param token token The token value to search for.
     * @return The VerificationToken entity matching the provided token, or null if not found.
     */
    VerificationToken findByToken(String token);
}
