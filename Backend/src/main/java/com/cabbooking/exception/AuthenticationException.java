package com.cabbooking.exception;

/**
 * Custom exception representing authentication failures.
 *
 * This exception is thrown when user credentials are invalid, or authentication
 * fails due to other reasons.
 *
 * Extends RuntimeException to allow unchecked propagation.
 *
 * Usage: - Thrown by authentication-related services, e.g., during login. -
 * Caught by global exception handlers to return appropriate HTTP error
 * responses.
 */
public class AuthenticationException extends RuntimeException {

    /**
     * Constructs a new AuthenticationException with the specified message.
     *
     * @param message The detail message explaining the cause of the exception.
     */
    public AuthenticationException(String message) {
        super(message);
    }
}
