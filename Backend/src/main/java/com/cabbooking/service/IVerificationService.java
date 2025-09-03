package com.cabbooking.service;

/*
 * Service interface for managing email verification.
 */
public interface IVerificationService {

    /**
     * Sends a verification link to the user's email.
     *
     * @param email the email address to send the verification link to
     */
    public void sendVerificationLink(String email);

    /**
     * Verifies the email using the provided token.
     *
     * @param token the verification token
     * @return true if the email is successfully verified, false otherwise
     */
    public boolean verifyToken(String token);
}
