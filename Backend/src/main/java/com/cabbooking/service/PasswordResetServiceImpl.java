package com.cabbooking.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cabbooking.model.AbstractUser;
import com.cabbooking.model.Admin;
import com.cabbooking.model.Driver;
import com.cabbooking.model.Customer;
import com.cabbooking.model.PasswordResetToken;
import com.cabbooking.repository.PasswordResetTokenRepository;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;

/*
 * Service class for password reset functionality.
 * Implements IPasswordResetService interface.
 * 
 * Main Responsibilities:
 * - Create and send password reset tokens to users.
 * - Reset passwords for users based on password reset tokens.
 * 
 * Security:
 * - Password reset tokens are stored in a database and are only valid for a certain amount of time.
 * - Password reset tokens are sent to users via email.
 */
@Service
public class PasswordResetServiceImpl implements IPasswordResetService {

    /*
     * Provides access to the profile service for user lookup.
     */
    @Autowired private IProfileService profileService; // Reuse the profile service to find users

    /*
     * Provides access to the password reset token repository.
     */
    @Autowired private PasswordResetTokenRepository tokenRepository;

    /*
     * Provides access to the admin repository.
     */
    @Autowired private AdminRepository adminRepository;

    /*
     * Provides access to the customer repository.
     */
    @Autowired private CustomerRepository customerRepository;

    /*
     * Provides access to the driver repository.
     */
    @Autowired private DriverRepository driverRepository;

    /*
     * Provides access to the email service.
     */
    @Autowired private IEmailService emailService;

    /*
     * Provides access to the password encoder.
     */
    @Autowired private PasswordEncoder passwordEncoder;

    /*
     * Creates a password reset token and sends an email to the user with a link to reset their password.
     * 
     * Workflow:
     * - User sends a request with their email address.
     * - Validates the email format.
     * - Calls the service layer to create and send a password reset link.
     * - Returns a success message if the email was sent.
     * - Returns an error response if the email is invalid or sending fails.
     * 
     * @param email The email address of the user to send the password reset link to.
     */
    @Override
    @Transactional
    public void createAndSendPasswordResetToken(String email) {
        AbstractUser user = profileService.getUserProfileByUsername(email) // Assuming username is email for this flow
                .orElseThrow(() -> new IllegalArgumentException("No user found with email: " + email));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, LocalDateTime.now().plusHours(1), user.getEmail());
        tokenRepository.save(resetToken);

        String subject = "Your Password Reset Request";
        String resetUrl = "http://your-frontend-url/reset-password?token=" + token;
        String message = "To reset your password, click the link below:\n" + resetUrl;
        
        emailService.sendSimpleEmail(user.getEmail(), subject, message);
    }

    /*
     * Resets the password for a user based on a password reset token.
     * 
     * Workflow:
     * - User sends a request with the reset token and new password.
     * - Validates the request data.
     * - Calls the service layer to reset the password.
     * - Returns a success message if the password was reset.
     * - Returns an error response if the token is invalid or expired.
     * 
     * @param token The password reset token.
     * @param newPassword The new password to set for the user.
     * @return True if the password was successfully reset, false otherwise.
     */
    @Override
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false; // Token is invalid or expired
        }

        AbstractUser user = profileService.getUserProfileByUsername(resetToken.getUserEmail())
                .orElseThrow(() -> new IllegalStateException("User associated with token not found."));

        user.setPassword(passwordEncoder.encode(newPassword));
        saveUser(user);

        tokenRepository.delete(resetToken); // Invalidate the token after use
        return true;
    }

    /**
     * Helper method to save a user to the correct repository based on their type.
     * @param user The user object to save.
     */
    private void saveUser(AbstractUser user) {
        if (user instanceof Admin) {
            adminRepository.save((Admin) user);
        } else if (user instanceof Customer) {
            customerRepository.save((Customer) user);
        } else if (user instanceof Driver) {
            driverRepository.save((Driver) user);
        }
    }
}