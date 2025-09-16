package com.cabbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cabbooking.model.PasswordResetToken;

/*
 * PasswordResetTokenRepository interface for managing PasswordResetToken entities.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /*
     * Finds a PasswordResetToken entity based on the provided token.
     * 
     * @param token The token to search for.
     * @return The PasswordResetToken entity if found; otherwise null.
     */
    PasswordResetToken findByToken(String token);
}
