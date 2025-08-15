package com.cabbooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cabbooking.model.BlacklistedToken;
import com.cabbooking.repository.BlacklistedTokenRepository;

/**
 * Service responsible for handling user logout functionality.
 *
 * Main Responsibilities:
 * - Implements the token blacklisting mechanism for JWT-based logout.
 * - Interacts with the BlacklistedTokenRepository to invalidate tokens.
 *
 * Workflow:
 * - The AuthController calls the blacklistToken method when a user hits the /logout endpoint.
 * - The service takes the user's JWT and saves it to the database, effectively invalidating it
 * for future requests.
 */
@Service
public class LogoutServiceImpl implements ILogoutService {

    // Repository for managing the persistence of blacklisted JWTs.
    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    /**
     * Blacklists a JWT to invalidate it for future use.
     *
     * @param token The JWT string to be blacklisted.
     */
    @Override
    public void blacklistToken(String token) {
        // Create a new entity to store the invalidated token.
        // For enhanced security, this could also store the token's expiry date
        // to allow for automatic cleanup of old tokens from the database.
        BlacklistedToken blacklisted = new BlacklistedToken();
        blacklisted.setToken(token);
        
        // Save the token to the blacklist table in the database.
        blacklistedTokenRepository.save(blacklisted);
    }
}