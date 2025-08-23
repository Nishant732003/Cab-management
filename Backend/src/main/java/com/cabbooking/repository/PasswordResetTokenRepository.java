package com.cabbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cabbooking.model.PasswordResetToken;

/*
 * Repository interface for PasswordResetToken entities.
 *
 * Extends JpaRepository to provide standard CRUD operations and pagination
 * support for PasswordResetToken entities with Long as the primary key type.
 *
 * Declares a custom query method to find PasswordResetToken by token, which is
 * important for password reset functionality.
 * 
 * This repository is responsible for managing PasswordResetToken entities, which
 * are used for password reset functionality in the application.
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