package com.cabbooking.service;

/**
 * Service interface for handling password reset operations.
 */
public interface IPasswordResetService {

    /*
     * Creates and sends a password reset token to the provided email address.
     * 
     * @param email The email address to send the password reset token to.
     */
    void createAndSendPasswordResetToken(String email);

    /*
     * Resets the password for the user associated with the provided token.
     * 
     * @param token The password reset token.
     * @param newPassword The new password to set for the user.
     * @return True if the password was successfully reset, false otherwise.
     */
    boolean resetPassword(String token, String newPassword);
}