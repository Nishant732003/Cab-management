package com.cabbooking.service;

/**
 * Service interface for user logout operations.
 */
public interface ILogoutService {

    /**
     * Blacklists a JWT to invalidate it for all future requests.
     *
     * @param token The JWT string to be blacklisted.
     */
    void blacklistToken(String token);
}