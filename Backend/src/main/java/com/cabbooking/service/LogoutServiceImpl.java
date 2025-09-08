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
 * Dependencies:
 * - BlacklistedTokenRepository for storing and retrieving invalidated tokens.
 */
@Service
public class LogoutServiceImpl implements ILogoutService {

    // Repository for managing the persistence of blacklisted JWTs.
    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    /**
     * Blacklists a JWT to invalidate it for future use.
     *
     * Workflow:
     * - Creates a new BlacklistedToken entity to represent the invalidated token.
     * - Saves the token to the blacklist table in the database.
     * 
     * @param token The JWT string to be blacklisted.
     */
    @Override
    public void blacklistToken(String token) {
        BlacklistedToken blacklisted = new BlacklistedToken();
        blacklisted.setToken(token);
        
        // Save the token to the blacklist table in the database.
        blacklistedTokenRepository.save(blacklisted);
    }
}