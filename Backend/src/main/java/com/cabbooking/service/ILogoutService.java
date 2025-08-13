package com.cabbooking.service;

/**
 * Service interface for user logout operations.
 *
 * Main Responsibilities:
 * - Defines the contract for all business logic related to logging a user out.
 * - Abstracts the implementation of token invalidation.
 *
 * Workflow:
 * - Implementations of this interface will be injected into controllers that handle user logout.
 * - It provides a clear method for invalidating a user's session token.
 */
public interface ILogoutService {

    /**
     * Blacklists a JWT to invalidate it for all future requests.
     *
     * @param token The JWT string to be blacklisted.
     */
    void blacklistToken(String token);
}